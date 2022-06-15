package com.codetreatise.thuydienapp.config.ftp;

import com.codetreatise.thuydienapp.config.SystemArg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String menuName;
    private Date nextTimeSend = new Date();

    public Boolean checkReady()  {
        try {
            if(nextTimeSend == null) {
                nextTimeSend = new Date();
            }
            Date now = new Date();
            now.setSeconds(0);
            return SystemArg.LOGIN && ready && new Date().after(nextTimeSend) && now.getMinutes() % timeSchedule  == 0;
        } catch (NullPointerException e) {
            return false;
        }

    }
    public void autoNextTime() {
        Date now = new Date();
        now.setSeconds(0);
        this.nextTimeSend = new Date(now.getTime() + 60*1000);
    }
}
