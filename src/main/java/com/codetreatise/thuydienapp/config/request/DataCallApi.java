package com.codetreatise.thuydienapp.config.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DataCallApi implements Serializable {
    private Float value;
    private String key;
    private String thoigian;
    private String nguon;
    private String mathongso;
    @JsonIgnore
    private Integer modbusDataId;
}
