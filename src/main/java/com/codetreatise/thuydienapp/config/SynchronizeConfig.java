package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.config.request.LoginRequest;
import com.codetreatise.thuydienapp.config.request.ObjectSendApi;
import com.codetreatise.thuydienapp.repository.DataReceiveRepository;
import com.codetreatise.thuydienapp.repository.ResultRepository;
import com.codetreatise.thuydienapp.view.FxmlView;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableScheduling
public class SynchronizeConfig {
    @Lazy
    @Autowired
    private StageManager stageManager;
    @Value("${api.token.valid}")
    private String validTokenUrl;
    @Value("${api.login}")
    private String loginUrl;
    @Lazy
    @Autowired
    private DataReceiveRepository dataReceiveRepository;
    @Autowired
    private Environment env;
    Logger logger = LoggerFactory.getLogger(SynchronizeConfig.class);
    @Autowired
    private ResultRepository resultRepository;


    @PostConstruct
    public void updateApiUrl() {
        SystemArg.API_CALL_URL = env.getProperty("api.post.savequantrac");
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


    }
    public SynchronizeConfig() {

    }

    @Scheduled( initialDelay = 60* 1000, fixedDelay = 3 * 1000)
    public void loginCheck() throws IOException {
        if(SystemArg.LOGIN) {
            AtomicReference<String> jsonBody = new AtomicReference<>("");
            AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RestTemplate restTemplate = new RestTemplate();
            if(SystemArg.TOKEN.equals("")) {
                try {
                    DataConfig.getHostsList();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            }
            jsonBody.set(SystemArg.TOKEN);
            HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
            try {
                response.set(restTemplate.postForEntity(validTokenUrl, request, String.class));
                SystemArg.LOGIN = response.get().getBody().equals(Boolean.TRUE.toString());
                DataConfig.saveFavorites(null);
            } catch (HttpClientErrorException ex) {
                if(ex.getStatusCode().value() == HttpStatus.FORBIDDEN.value()) {
                    SystemArg.LOGIN = false;
                    DataConfig.saveFavorites(null);
                    stageManager.switchScene(FxmlView.LOGIN);
                }
                ex.printStackTrace();
            }
        } else {
            stageManager.switchScene(FxmlView.LOGIN);
        }

    }


    @Scheduled( initialDelay = 3 * 1000, fixedDelay = 30 * 1000)
    public void getData() {

        if(!SystemArg.checkTimeScheduleSyncModbus()) {
            return;
        }

        ModbusMaster modbusMaster = ModbusMaster.builder()
                .ip(SystemArg.MODBUS_IP)
                .port(SystemArg.MODBUS_PORT)
                .status(1)
                .build();

        try {
            ModbusClient modbusClient = new ModbusClient();

            modbusClient.Connect(modbusMaster.getIp(), modbusMaster.getPort());
            modbusClient.setUnitIdentifier(SystemArg.UNIT);

        Date now = new Date();
        now.setSeconds(0);
        now.setMinutes((now.getMinutes() / 5) * 5);
        List<Data> dataList = SystemArg.DATA_LIST;

            dataList.forEach(e -> {
                try {

                    float arg = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(e.getAddress(), e.getQuantity()));
                    logger.info(e.getKey() + " : " + arg);
                    dataReceiveRepository.save(
                            DataReceive.builder()
                                    .data(e)
                                    .status(0)
                                    .thoigian(now)
                                    .value(arg)
                                    .build()
                    );
                } catch (ModbusException | IOException modbusException) {
                    logger.error(modbusException.getMessage());
                }
            });

            modbusClient.Disconnect();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            SystemArg.setNextTimeScheduleSyncModbus();

        }

    }

    @Scheduled( initialDelay = 3 * 1000, fixedDelay = 30 * 1000)
    public void syncCallAPi() {


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
                    e.setStatus(1);
                    dataReceiveRepository.save(e);
                    Thread.sleep(500);

                } catch (HttpServerErrorException  ex) {
                    ex.printStackTrace();
                    ResponseEntity<String> responseEntity = new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getStatusCode());
                    response.set(responseEntity);
                    e.setStatus(1);
                    dataReceiveRepository.save(e);

                } catch (Exception exception) {
                    logger.error(exception.getMessage());
                }
                finally {
                    resultRepository.save(Result.builder()
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
