package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
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
    private String timeString;

    public String getTimeString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(time);
    }
}
