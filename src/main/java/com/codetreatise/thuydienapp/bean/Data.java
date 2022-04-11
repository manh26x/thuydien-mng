package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data  implements Serializable {
    private String key;
    private String nguon;
    private String tenChiTieu;
    private String dvt;
    private Integer address;
    private Integer quantity;
    private Integer status;
    private String maThongSo;

    @Override
    public String toString() {
        return tenChiTieu + " - " + maThongSo;
    }
}
