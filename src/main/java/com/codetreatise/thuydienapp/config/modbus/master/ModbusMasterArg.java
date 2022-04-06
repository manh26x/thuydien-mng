package com.codetreatise.thuydienapp.config.modbus.master;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class ModbusMasterArg implements Serializable {
    private Integer port;
    private Boolean ready;
    private Boolean isRunning;
    private String name;

    public ModbusMasterArg() {
        this.port = 502;
        name = "";
        this.ready = false;
        this.isRunning = false;
    }

}
