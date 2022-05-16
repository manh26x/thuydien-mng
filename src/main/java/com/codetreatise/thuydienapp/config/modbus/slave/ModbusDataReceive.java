package com.codetreatise.thuydienapp.config.modbus.slave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModbusDataReceive {
    private double value;
    private String address;
    private String quantity;

}
