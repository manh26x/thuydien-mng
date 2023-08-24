package com.codetreatise.thuydienapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIDataModel {
    private Date time;
    private String maThamSo;
    private Float value;
    private Integer code;

    public Float getValue() {
        return Math.round(value*100) / 100.0f;
    }
}
