package com.codetreatise.thuydienapp.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoCTData implements Serializable {
    private String key;
    private Integer codeResponse;
    private Float value;
    private Integer modbusDataId;
}
