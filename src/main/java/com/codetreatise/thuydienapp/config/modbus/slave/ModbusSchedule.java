package com.codetreatise.thuydienapp.config.modbus.slave;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.repository.DataReceiveJdbc;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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


    private synchronized void getData() {

        if(!SystemArg.checkTimeScheduleSyncModbus()) {
            return;
        }
        StringBuilder content = new StringBuilder();
        ModbusMaster modbusMaster = ModbusMaster.builder()
                .ip(SystemArg.MODBUS_IP)
                .port(SystemArg.MODBUS_PORT)
                .status(1)
                .build();
        AtomicBoolean isError = new AtomicBoolean(false);
        AtomicReference<String> message = new AtomicReference<>("");
        try {

            ModbusClient modbusClient = new ModbusClient();

            modbusClient.Connect(modbusMaster.getIp(), modbusMaster.getPort());
            modbusClient.setUnitIdentifier(SystemArg.UNIT);

            Date now = new Date();
            now.setSeconds(0);
            now.setMinutes((now.getMinutes() / 5) * 5);
            List<ModbusParam> dataList = SqliteJdbc.getInstance().getModbusParams();

            dataList.forEach(e -> {
                try {

                    content.append(e.toString());
                    float arg = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(e.getAddress(), 2));
                    content.append("value: ").append(arg);
                    DataReceiveJdbc.getInstance().insert(
                            ModbusData.builder()
                                    .timeReceive(now)
                                    .name(e.getName())
                                    .value(Math.round(arg* 100) / 100.0f)
                                    .build()
                    );
                } catch (ModbusException | IOException modbusException) {
                    logger.error(modbusException.getMessage());
                    isError.set(true);
                    message.set(modbusException.getMessage());
                    content.append(modbusException.getMessage());
                }
            });

            modbusClient.Disconnect();
        } catch (IOException | SQLException e) {
            logger.error(e.getMessage());
            isError.set(true);
            message.set(e.getMessage());
            content.append(" ").append(e.getMessage());
        } finally {
            SystemArg.setNextTimeScheduleSyncModbus();
        }

        EventTrigger.getInstance().setChange();
        EventTrigger.getInstance().notifyObservers(EventObject.builder()
                .type(isError.get() ? Constants.CONST_ERROR : Constants.CONST_SUCCESS)
                .dataError(DataError.builder()
                        .menuName("Modbus")
                        .type(Constants.MODBUS_TYPE)
                        .message(content.toString())
                        .title("Modbus Error")
                        .build())
                .build());

    }
}
