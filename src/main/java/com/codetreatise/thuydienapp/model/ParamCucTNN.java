package com.codetreatise.thuydienapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamCucTNN {
    private String thongSoDo;
    private String dvt;
    private String kyHieuTram;
    private Integer address;
    private String name;
}
