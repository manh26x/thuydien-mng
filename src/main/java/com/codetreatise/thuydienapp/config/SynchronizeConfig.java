package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.ModbusMaster;
import com.codetreatise.thuydienapp.bean.Result;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.config.request.ObjectSendApi;
import com.codetreatise.thuydienapp.repository.DataReceiveRepository;
import com.codetreatise.thuydienapp.repository.DataRepository;
import com.codetreatise.thuydienapp.repository.ModbusMasterRepository;
import com.codetreatise.thuydienapp.repository.ResultRepository;
import com.codetreatise.thuydienapp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableScheduling
public class SynchronizeConfig {
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
        } catch (Exception e) {}

    }

    public SynchronizeConfig() {

    }

    @Scheduled( initialDelay = 3 * 1000, fixedDelay = 30 * 1000)
    public void getData() {

        if(SystemArg.checkTimeScheduleSyncModbus()) {
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

        Date now = new Date();
        now.setSeconds(0);
        now.setMinutes((now.getMinutes() / 5) * 5);
        List<Data> dataList = SystemArg.DATA_LIST;

            dataList.forEach(e -> {
                try {
                    if(e.getUiid() == null) {
                        e.setUiid((byte) 1);
                    }
                    modbusClient.setUnitIdentifier(e.getUiid());
                    float arg = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadInputRegisters(e.getAddress(), e.getQuantity()));
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

        if (SystemArg.checkTimeScheduleCallApi()) {
            return;
        }
        callApi();
    }

    public void callApi() {

        AtomicReference<String> jsonBody = new AtomicReference<>("");
        AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
            List<DataCallApi> datas = new ArrayList<>();
            dataReceiveRepository.findAllByStatus(0).forEach(e -> {
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
                            .username(SystemArg.API_USERNAME)
                            .pass(SystemArg.API_PASSWORD)
                            .datas(datas)
                            .build()));
                    HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
                    response.set(restTemplate.postForEntity(SystemArg.API_CALL_URL, request, String.class));
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
                                .response(response.get().getBody())
                                .build());
                        SystemArg.setNextTimeScheduleCallApi();
                    }

            });
    }
}
