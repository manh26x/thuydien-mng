package com.codetreatise.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@lombok.Data
public class Data {
    @Id
    private String key;
    private String nguon;
    private String tenChiTieu;
    private String dvt;
    private Integer address;
    private Integer quantity;
    private Integer status;
}
