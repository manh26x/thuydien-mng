package com.codetreatise.thuydienapp.config.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@Data
@AllArgsConstructor
@Builder
public class FtpArgSaved implements Serializable {
    private HashMap<String, FtpConfigArg> ftpConfigArg;
    public FtpArgSaved() {
        ftpConfigArg = new HashMap<>();
    }
}
