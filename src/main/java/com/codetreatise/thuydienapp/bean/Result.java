package com.codetreatise.thuydienapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Long id;

    private String api;

    private String response;

    private String request;

    private Integer codeResponse;

    private DataReceive dataReceive;

    private Date timeSend;
    private String apiName;

    public String getTimeSendString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(timeSend);
    }

}
