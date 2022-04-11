package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModbusMaster {
    @Id
    @GeneratedValue
    private Long id;
    private String ip;
    private Integer port;
    private Integer status;

}
