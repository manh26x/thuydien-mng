package com.codetreatise.thuydienapp.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class ModbusData {
    private Date timeReceive;
    private String name;
    private Float value;
}
