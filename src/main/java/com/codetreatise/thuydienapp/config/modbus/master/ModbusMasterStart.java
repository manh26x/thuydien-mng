package com.codetreatise.thuydienapp.config.modbus.master;

import de.re.easymodbus.server.ModbusServer;
import lombok.SneakyThrows;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.TimerTask;

public class ModbusMasterStart extends TimerTask {

    private ModbusServer modbusServer;

    private static ModbusMasterStart instance;

    private ModbusMasterStart() {
        modbusServer = new ModbusServer();
    }

    public static ModbusMasterStart getInstance() {
        if (instance == null) {
            instance = new ModbusMasterStart();
        }
        return instance;
    }

    public void createModbusMaster() throws IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        ModbusMasterArg modbusMasterArg = ModbusMasterConfig.getModbusConfig();
        if (modbusMasterArg.getReady() && !modbusServer.getServerRunning()) {
            modbusServer.setPort(modbusMasterArg.getPort());
            modbusServer.setName(modbusMasterArg.getName());
            modbusServer.udpFlag = true;

            modbusServer.Listen();
        }

    }

    @SneakyThrows
    @Override
    public void run() {
        createModbusMaster();
    }
}
