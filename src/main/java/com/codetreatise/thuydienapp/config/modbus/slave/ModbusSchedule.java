package com.codetreatise.thuydienapp.config.modbus.slave;

import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;
import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.ModbusMaster;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.repository.DataReceiveJdbc;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class ModbusSchedule extends TimerTask {

    Logger logger = LoggerFactory.getLogger(ModbusSchedule.class);
    private static ModbusSchedule instance;

    public static ModbusSchedule getInstance() {
        if (instance == null) {
            instance = new ModbusSchedule();
        }
        return instance;
    }

    @Override
    public void run() {
        getData();
    }


    private void getData() {

        if(!SystemArg.checkTimeScheduleSyncModbus()) {
            return;
        }

        ModbusMaster modbusMaster = ModbusMaster.builder()
                .ip(SystemArg.MODBUS_IP)
                .port(SystemArg.MODBUS_PORT)
                .status(1)
                .build();
        boolean isError = false;
        String message = "";
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
                    DataReceiveJdbc.getInstance().insert(
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
            isError= true;
            message = e.getMessage();
        } finally {
            SystemArg.setNextTimeScheduleSyncModbus();
        }

        EventTrigger.getInstance().setChange();
        EventTrigger.getInstance().notifyObservers(EventObject.builder()
                .type(isError? Constants.CONST_ERROR : Constants.CONST_SUCCESS)
                .dataError(DataError.builder()
                        .menuName("Modbus")
                        .type(Constants.MODBUS_TYPE)
                        .message(message)
                        .build())
                .build());

    }
}
