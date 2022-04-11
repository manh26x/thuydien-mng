package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.config.request.ObjectSendApi;
import com.codetreatise.thuydienapp.repository.ResultRepository;
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
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SynchronizeConfig extends TimerTask {

    private static SynchronizeConfig instance = new SynchronizeConfig();;

    private final ResultRepository resultRepository;
    public static SynchronizeConfig getInstance() {
        return instance;
    }

    public SynchronizeConfig() {
        try {

            DataConfig.getHostsList();

        } catch (Exception ignored) {
        } finally {
            if(SystemArg.API_LIST.size() == 0) {
                ApiConfig apiConfig = new ApiConfig();
                apiConfig.setApiCallReady(SystemArg.API_CALL_API_READY);
                apiConfig.setName(SystemArg.API_CALL_URL);
                apiConfig.setId(0);
                apiConfig.setUrl(SystemArg.API_CALL_URL);
                apiConfig.setUsername(SystemArg.API_USERNAME);
                apiConfig.setPassword(SystemArg.API_PASSWORD);
                apiConfig.setTimeScheduleCallApi(SystemArg.TIME_SCHEDULE_CALL_API);
                SystemArg.API_LIST.add(apiConfig);
            }
        }


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

            AtomicReference<String> jsonBody = new AtomicReference<>("");
            AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            List<DataCallApi> datas = new ArrayList<>();

            resultRepository.findNotSend(apiConfig.getUrl()).forEach(e -> {
                try {
                    datas.clear();
                    datas.add(DataCallApi.builder()
                            .key(e.getData().getKey())
                            .nguon(e.getData().getNguon())
                            .value(e.getValue())
                            .thoigian(simpleDateFormat.format(e.getThoigian()))
                            .mathongso(e.getData().getMaThongSo())
                            .build());
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
                    Thread.sleep(500);

                } catch (HttpServerErrorException  ex) {
                    ex.printStackTrace();
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getStatusCode());
                    response.set(responseEntity);
                    e.setStatus(1);

                } catch (Exception exception) {
                    log.error(exception.getMessage());
                }
                finally {
                    resultRepository.insert(Result.builder()
                            .id(null)
                            .request(jsonBody.get())
                            .api(SystemArg.API_CALL_URL)
                            .codeResponse(response.get().getStatusCodeValue())
                            .timeSend(new Date())
                            .api(apiConfig.getUrl())
                                    .dataReceive(e)
                            .response(response.get().getBody())
                            .build());
                    apiConfig.autoNextTimeScheduleCallApi();
                }

            });
        });

    }
}
