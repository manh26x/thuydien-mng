package com.codetreatise.thuydienapp.model;

import com.codetreatise.thuydienapp.bean.ModbusParam;
import lombok.Data;

@Data
public class ModbusParamData extends ModbusParam {
    private Float value;

    public ModbusParamData(ModbusParam e) {
        this.dvt = e.getDvt();
        this.address = e.getAddress();
        this.name = e.getName();
        this.value = null;
    }
}
