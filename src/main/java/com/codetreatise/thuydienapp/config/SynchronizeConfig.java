package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.request.CucTNNApiData;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.config.request.ObjectSendApi;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.repository.BoCTRepository;
import com.codetreatise.thuydienapp.repository.CucTNNRepository;
import com.codetreatise.thuydienapp.repository.DataErrorRepository;
import com.codetreatise.thuydienapp.repository.ResultRepository;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SynchronizeConfig extends TimerTask {

    private static final SynchronizeConfig instance = new SynchronizeConfig();

    private final ResultRepository resultRepository;
    public static SynchronizeConfig getInstance() {
        return instance;
    }

    public SynchronizeConfig() {
        resultRepository = ResultRepository.getInstance();
    }

    @Override
    public void run() {
        callApi();
    }

    public synchronized void callApi() {

      SystemArg.API_LIST.stream().filter(ApiConfig::checkTimeScheduleCallApi)
                .forEach(apiConfig -> {
                    try {
                        log.info("start Call API: {}", apiConfig.getUrl() );

                        if(apiConfig.getName().equals(Constants.BO_CT_NAME)) {
                            sendDataBoCT(apiConfig);
                        }
                        if(apiConfig.getName().equals(Constants.CUC_TNN_NAME)) {
                            sendDataCucTNN(apiConfig);
                        }
                        apiConfig.autoNextTimeScheduleCallApi();
                    } catch (Exception e) {
                        log.error("error call api",e);
                    }
                });

    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
    private void sendDataCucTNN(ApiConfig apiConfig) {
        AtomicReference<String> jsonBody = new AtomicReference<>("");
        AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("yyyyMMddHHmmss");
        AtomicBoolean isException = new AtomicBoolean(false);
        AtomicReference<String> customMessage = new AtomicReference<>();
        StringBuilder content = new StringBuilder();
        AtomicReference<CucTNNApiData> resultData = new AtomicReference<>(CucTNNRepository.getInstance().getInfo());
        CucTNNRepository.getInstance().findAllDataNotSend().forEach(contentData -> {
            try {
                resultData.get().setThoiGianGui(simpleDateFormat.format(new Date()));
                resultData.get().setKyHieuTram(contentData.getKyHieuTram());
                resultData.get().setNoiDung(new ArrayList<>());
                resultData.get().getNoiDung().add(contentData.toList(simpleDateFormat));
                log.info("START send data {}", resultData.get());
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(apiConfig.getUsername(), apiConfig.getPassword());
                RestTemplate restTemplate = new RestTemplate();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                jsonBody.set(ow.writeValueAsString(resultData));

                HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
                response.set(restTemplate.exchange(apiConfig.getUrl(), HttpMethod.POST, request, String.class));
                customMessage.set("");
            } catch (HttpServerErrorException  ex) {
                log.error(ex.getMessage());
                ResponseEntity<String> responseEntity = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
                response.set(responseEntity);
                customMessage.set(ex.getResponseBodyAsString());
                content.append(ex.getResponseBodyAsString());
                isException.set(true);
            } catch (Exception exception) {
                response.set( new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                log.error(exception.getMessage());
                isException.set(true);
                content.append(" ").append(exception.getMessage());
            }
            finally {
                CucTNNRepository.getInstance().insertCucTNNData(contentData, response.get().getStatusCode());
                DataError dataError = DataError.builder()
                        .type(Constants.API_TYPE)
                        .title(isException.get() ? "API ERROR" : "API SUCCESS")
                        .message(content.toString())
                        .menuName(apiConfig.getName())
                        .build();
                if(isException.get()) {
                    DataErrorRepository.getInstance().insert(dataError);
                }
                EventTrigger.getInstance().notifyObservers(EventObject.builder()
                        .type(isException.get() ? Constants.CONST_ERROR: Constants.CONST_SUCCESS)
                        .dataError(dataError)
                        .build());
                EventTrigger.getInstance().setChange();
            }

        });
    }

    private void sendDataBoCT(ApiConfig apiConfig) {
        AtomicReference<String> jsonBody = new AtomicReference<>("");
        AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        List<DataCallApi> datas = new ArrayList<>();
        AtomicBoolean isException = new AtomicBoolean(false);
        AtomicReference<String> customMessage = new AtomicReference<>();
        StringBuilder content = new StringBuilder();
        AtomicReference<BoCTData> resultData = new AtomicReference<>();
        BoCTRepository.getInstance().findAllDataNotSend().forEach(dataCallApi -> {
            try {
                datas.clear();
                datas.add(dataCallApi);
                content.append(dataCallApi.toString());
                log.info("START send data {}", dataCallApi);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                RestTemplate restTemplate = new RestTemplate();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                jsonBody.set(ow.writeValueAsString(ObjectSendApi.builder()
                        .username(apiConfig.getUsername())
                        .pass(apiConfig.getPassword())
                        .datas(datas)
                        .build()));
                HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
                response.set(restTemplate.postForEntity(apiConfig.getUrl(), request, String.class));
                customMessage.set("");
                resultData.set(BoCTData.builder()
                        .value(dataCallApi.getValue())
                        .key(dataCallApi.getKey())
                        .modbusDataId(dataCallApi.getModbusDataId())
                        .codeResponse(-1)
                        .build());
            } catch (HttpServerErrorException  ex) {
                log.error(ex.getMessage());
                ResponseEntity<String> responseEntity = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
                response.set(responseEntity);
                customMessage.set(ex.getResponseBodyAsString());
                content.append(ex.getResponseBodyAsString());
                isException.set(true);
            } catch (Exception exception) {
                response.set( new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                log.error(exception.getMessage());
                isException.set(true);
                content.append(" ").append(exception.getMessage());
            }
            finally {
                resultData.get().setCodeResponse(response.get().getStatusCodeValue());
                BoCTRepository.getInstance().insertBoCTData(resultData.get());
                DataError dataError = DataError.builder()
                        .type(Constants.API_TYPE)
                        .title(isException.get() ? "API ERROR" : "API SUCCESS")
                        .message(content.toString())
                        .menuName(apiConfig.getName())
                        .build();
                if(isException.get()) {
                    DataErrorRepository.getInstance().insert(dataError);
                }
                EventTrigger.getInstance().notifyObservers(EventObject.builder()
                        .type(isException.get() ? Constants.CONST_ERROR: Constants.CONST_SUCCESS)
                        .dataError(dataError)
                        .build());
                EventTrigger.getInstance().setChange();
            }

        });

    }
}
