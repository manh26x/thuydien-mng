package com.codetreatise.thuydienapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamBoCT implements Serializable {
    private String key;
    private String nguon;
    private String tenChiTieu;
    private String dvt;
    private Integer address;
    private String name;
    private String maThamSo;
}
