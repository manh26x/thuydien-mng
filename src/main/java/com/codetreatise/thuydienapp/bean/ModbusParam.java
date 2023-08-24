package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModbusParam {
    protected String name;
    protected String dvt;
    protected int address;

    public String toString() {
        return name;
    }
}
