package com.codetreatise.thuydienapp.bean;

import com.codetreatise.thuydienapp.config.SystemArg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ApiConfig implements Serializable {
    private int id;
    private String name;
    private String url;
    private Integer timeScheduleCallApi;
    private Date nextTimeScheduleCallApi;
    private String username;
    private String password;
    private boolean apiCallReady;

    public ApiConfig() {
        url = "";
        name = url;
        timeScheduleCallApi = 1000 * 60 * 10;
        nextTimeScheduleCallApi = new Date();
        apiCallReady = Boolean.FALSE;
    }


    public void autoNextTimeScheduleCallApi() {
        Date now = new Date();
        now.setSeconds(0);
        nextTimeScheduleCallApi = new Date(now.getTime() + timeScheduleCallApi);
    }

    public boolean checkTimeScheduleCallApi() {
        return SystemArg.LOGIN && apiCallReady && new Date().after(nextTimeScheduleCallApi) && !url.equals("");
    }


}
