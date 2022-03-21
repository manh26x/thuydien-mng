package com.codetreatise.thuydienapp.config.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataCallApi implements Serializable {
    private Float value;
    private String key;
    private String thoigian;
    private String nguon;
    private String mathongso;
}
