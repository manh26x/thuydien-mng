package com.codetreatise.thuydienapp.bean;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataReceive {
    private Long id;
    private Data data;


    private Date thoigian;
    private Float value;

    private Integer status;


}
