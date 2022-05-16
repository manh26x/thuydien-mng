package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModbusDataReceiveTable {
    private Integer address;
    private String tenChiTieu;
    private String maThongSo;
    private Float value;
    private Date time;
}
