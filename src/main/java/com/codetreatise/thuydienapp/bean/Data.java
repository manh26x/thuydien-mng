package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Data  implements Serializable {
    private String key;
    private String nguon;
    private String tenChiTieu;
    private String dvt;
    private Integer address;
    private Integer quantity;
    private Integer status;
    private String maThongSo;

}
