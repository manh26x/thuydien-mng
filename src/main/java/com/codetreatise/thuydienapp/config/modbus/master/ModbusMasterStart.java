package com.codetreatise.thuydienapp.config.modbus.master;

import de.re.easymodbus.server.ModbusServer;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

@Slf4j
public class ModbusMasterStart  {

    private final ModbusServer modbusServer;

    private static ModbusMasterStart instance;
    private ModbusMasterStart() {
        modbusServer = new ModbusServer();
    }
    public static ModbusMasterStart getInstance() {
        if(instance == null) {
            instance = new ModbusMasterStart();
        }
        return instance;
    }

    public void reloadModbus() {
        ModbusMasterArg modbusMasterArg = null;
        try {
            modbusMasterArg = ModbusMasterConfig.getModbusConfig();
            createModbusMaster(modbusMasterArg);
            stopModbus(modbusMasterArg);
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

    }


    public void createModbusMaster(ModbusMasterArg modbusMasterArg) throws IOException{
        getInstance();
        if(Boolean.TRUE.equals(modbusMasterArg.getReady()) && Boolean.FALSE.equals(modbusServer.getServerRunning())) {
            modbusServer.setPort(modbusMasterArg.getPort());
            modbusServer.setName(modbusMasterArg.getName());
            modbusServer.udpFlag=true;
            modbusServer.Listen();
            log.info("START modbus server: " + modbusServer.getName() + " on port: " + modbusServer.getPort());
            log.info("Modbus server is running? " +        modbusServer.getServerRunning());
        }

    }

    public void stopModbus(ModbusMasterArg modbusMasterArg)  {
        if (Boolean.FALSE.equals(modbusMasterArg.getReady() && modbusServer.getServerRunning())) {
            try {
                modbusServer.StopListening();
            }catch (Exception ignore){
            }
            log.info("STOP modbus server: " + modbusServer.getName() + " on port: " + modbusServer.getPort());
            log.info("Modbus server is running? " + modbusServer.getServerRunning());
        }
    }


}
