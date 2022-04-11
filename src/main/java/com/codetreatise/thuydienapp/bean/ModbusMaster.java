package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModbusMaster {
    private Long id;
    private String ip;
    private Integer port;
    private Integer status;

}
