package com.codetreatise.thuydienapp.config.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
}
