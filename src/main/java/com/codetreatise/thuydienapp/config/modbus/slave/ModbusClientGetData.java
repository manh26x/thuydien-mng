package com.codetreatise.thuydienapp.config.modbus.slave;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

import java.io.IOException;

public class ModbusClientGetData {

    private ModbusClient modbusClient;
    private static ModbusClientGetData instance;


    private ModbusClientGetData() {
        modbusClient = new ModbusClient();
    }

    public static ModbusClientGetData getInstance() {
        if(instance == null) {
            instance = new ModbusClientGetData();
        }
        return instance;
    }

    public void connect(String ipAddress, Integer port, Byte slaveId) throws IOException {
        modbusClient.Connect(ipAddress, port);
        modbusClient.setUnitIdentifier(slaveId);
    }

    public Float getValue(int address, int quantity) throws IOException, ModbusException {
        float value = 0F;
        try {
            value = ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(address, quantity));
        }  catch (Exception e) {
            disconnect();
        }
        return value;
    }

    public void disconnect() throws IOException {
        modbusClient.Disconnect();
    }

    public boolean isConnected() {
        return modbusClient.isConnected();
    }
}
