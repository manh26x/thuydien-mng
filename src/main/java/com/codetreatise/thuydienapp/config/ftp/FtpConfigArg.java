package com.codetreatise.thuydienapp.config.ftp;

import com.codetreatise.thuydienapp.config.SystemArg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FtpConfigArg implements Serializable {
    private String ipAddress;
    private int port;
    private String account;
    private String password;
    private String protocol;
    private String localWorkingDirectory;
    private String remoteWorkingDirectory;
    private Boolean isPassive;
    private Boolean ready;
    private Integer timeSchedule;
    private String transferDirectory;
    private Date nextTimeSend = new Date();

    public Boolean checkReady()  {
        try {
            if(nextTimeSend == null) {
                nextTimeSend = new Date();
            }
            return SystemArg.LOGIN && ready && new Date().after(nextTimeSend);
        } catch (NullPointerException e) {
            return false;
        }

    }
    public void autoNextTime() {
        Date now = new Date();
        now.setSeconds(0);
        this.nextTimeSend = new Date(now.getTime() + timeSchedule*60*1000);
    }
}
