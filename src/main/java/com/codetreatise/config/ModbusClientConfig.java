package com.codetreatise.config;

import com.codetreatise.bean.Data;
import com.codetreatise.bean.DataReceive;
import com.codetreatise.bean.ModbusMaster;
import com.codetreatise.bean.User;
import com.codetreatise.config.request.DataCallApi;
import com.codetreatise.config.request.ObjectSendApi;
import com.codetreatise.repository.DataReceiveRepository;
import com.codetreatise.repository.DataRepository;
import com.codetreatise.repository.ModbusMasterRepository;
import com.codetreatise.service.UserService;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class ModbusClientConfig {

    @Autowired
    private ModbusMasterRepository modbusMasterRepository;
    @Autowired
    private DataRepository dataRepository;
    @Autowired
    private DataReceiveRepository dataReceiveRepository;
    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;


    @PostConstruct
    public void updateApiUrl() {
        SystemArg.API_CALL_URL = env.getProperty("api.post.savequantrac");
    }

    public ModbusClientConfig() {

    }

    @Scheduled( initialDelay = 5 * 1000, fixedDelay = 10 * 1000)
    public void getData() {

        if(SystemArg.checkTimeScheduleSyncModbus()) {
            return;
        }

        ModbusMaster modbusMaster = modbusMasterRepository.findByStatus(1);

        try {
            ModbusClient modbusClient = new ModbusClient();

            modbusClient.Connect(modbusMaster.getIp(), modbusMaster.getPort());


        List<Data> dataList = dataRepository.findAllByStatus(1);

            dataList.forEach(e -> {
                try {
                    float arg = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadInputRegisters(e.getAddress(), e.getQuantity()));
                    System.out.println(e.getKey() + " : " + arg);
                    dataReceiveRepository.save(
                            DataReceive.builder()
                                    .data(e)
                                    .modbusMaster(modbusMaster)
                                    .status(0)
                                    .thoigian(new Date())
                                    .value(arg)
                                    .build()
                    );
                } catch (ModbusException | IOException modbusException) {
                    modbusException.printStackTrace();
                }
            });

            modbusClient.Disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SystemArg.setNextTimeScheduleSyncModbus();
        }

    }

    @Scheduled( initialDelay = 5 * 1000, fixedDelay = 10 * 1000)
    public void syncCallAPi() {

        if (SystemArg.checkTimeScheduleCallApi()) {
            return;
        }
        try {
            User user = userService.getActiveUser();
            List<DataCallApi> datas = new ArrayList<DataCallApi>();
            dataReceiveRepository.findAllByStatus(0).forEach(e -> {
                datas.add(DataCallApi.builder()
                        .key(e.getData().getKey())
                        .nguon(e.getData().getNguon())
                        .value(e.getValue())
                        .thoigian(e.getThoigian())
                        .build());
            });
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<ObjectSendApi> request = new HttpEntity<>(ObjectSendApi.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .datas(datas)
                    .build());
            ResponseEntity<String> response =  restTemplate.exchange(SystemArg.API_CALL_URL, SystemArg.HTTP_METHOD_CALL_API,request, String.class);
            System.out.println(response.getStatusCode());
            dataReceiveRepository.updateStatus(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
          SystemArg.setNextTimeScheduleCallApi();
        }



    }

}
