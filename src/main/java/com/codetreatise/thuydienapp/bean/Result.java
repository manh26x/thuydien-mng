package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    private String api;

    @Column(columnDefinition = "LONGVARCHAR")
    private String response;

    @Column(columnDefinition = "LONGVARCHAR")
    private String request;

    private Integer codeResponse;

    @ManyToOne
    @JoinColumn(name = "data_receive_id")
    private DataReceive dataReceive;

    private Date timeSend;

}
