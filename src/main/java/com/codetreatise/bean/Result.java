package com.codetreatise.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
@lombok.Data
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    private String api;


    @ManyToOne
    @JoinColumn(name="data_receive_id")
    private DataReceive data;

    @ManyToOne
    @JoinColumn(name="username")
    private User user;

    private Integer codeResponse;

    @Column(columnDefinition = "longtext")
    private String result;

    private Date timeSend;
}
