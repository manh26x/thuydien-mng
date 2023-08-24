package com.codetreatise.thuydienapp.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ModbusDataModel {
    private Date modTime;
    private Map<String, Float> data;

    public ModbusDataModel() {
        data = new HashMap<>();
    }

    public String getModTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(modTime);
    }
}
