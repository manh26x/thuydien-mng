package com.codetreatise.thuydienapp.bean;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataReceive {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "data_id")
    private Data data;


    private Date thoigian;
    private Float value;

    private Integer status;


}
