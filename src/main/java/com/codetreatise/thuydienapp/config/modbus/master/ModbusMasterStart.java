package com.codetreatise.thuydienapp.config.modbus.master;

import de.re.easymodbus.server.ModbusServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

@Slf4j
@Configuration
public class ModbusMasterStart {

    private static ModbusServer MODBUS_SERVER = new ModbusServer();

    @Scheduled(initialDelay = 5000, fixedDelay = 30000)
    public void createModbusMaster() throws IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        ModbusMasterArg modbusMasterArg = ModbusMasterConfig.getModbusConfig();
        if(modbusMasterArg.getReady() && !MODBUS_SERVER.getServerRunning()) {
            MODBUS_SERVER.setPort(modbusMasterArg.getPort());
            MODBUS_SERVER.setName(modbusMasterArg.getName());
            MODBUS_SERVER.Listen();
            log.info("START modbus server: " + MODBUS_SERVER.getName() + " on port: " + MODBUS_SERVER.getPort());
            log.info("Modbus server is running? " +        MODBUS_SERVER.getServerRunning());
        }

    }
}
