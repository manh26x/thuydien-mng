package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.config.request.ObjectSendApi;
import com.codetreatise.thuydienapp.event.EventTrigger;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
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

    @SneakyThrows
    @Override
    public void run() {
        callApi();
    }

    public void callApi() {

        SystemArg.API_LIST.stream().filter(ApiConfig::checkTimeScheduleCallApi)
                .forEach(apiConfig -> {
            log.info("start Call API: {}", apiConfig.getUrl() );
            AtomicReference<String> jsonBody = new AtomicReference<>("");
            AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            List<DataCallApi> datas = new ArrayList<>();
            AtomicBoolean isException = new AtomicBoolean(false);
            AtomicReference<String> customMessage = new AtomicReference<>();
            StringBuilder content = new StringBuilder();
            resultRepository.findNotSend(apiConfig).forEach(e -> {
                try {
                    datas.clear();
                    DataCallApi dataCallApi = DataCallApi.builder()
                            .key(e.getData().getKey())
                            .nguon(e.getData().getNguon())
                            .value(e.getValue())
                            .thoigian(simpleDateFormat.format(e.getThoigian()))
                            .mathongso(e.getData().getMaThongSo())
                            .build();
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
                } catch (HttpServerErrorException  ex) {
                    log.error(ex.getMessage());
                    ResponseEntity<String> responseEntity = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
                    response.set(responseEntity);
                    e.setStatus(1);
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
                    resultRepository.insert(Result.builder()
                            .id(null)
                            .request(jsonBody.get())
                            .dataReceive(e)
                            .api(SystemArg.API_CALL_URL)
                            .codeResponse(response.get().getStatusCodeValue())
                            .timeSend(new Date())
                            .api(apiConfig.getUrl())
                                    .dataReceive(e)
                            .response(response.get().getBody())
                            .apiName(apiConfig.getName())
                            .build());

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

                    apiConfig.autoNextTimeScheduleCallApi();

                });

    }
}
