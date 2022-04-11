package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.config.ftp.FtpConfigArg;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

public class AddFtpController {
    public TextField name;
    private final StageManager stageManager;

    public AddFtpController() {
        stageManager = StageManager.getInstance();
    }

    public void close(ActionEvent actionEvent) {
        stageManager.closeDialog();
    }

    public void save(ActionEvent actionEvent) throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        ApiConfig apiConfig =new ApiConfig();
        apiConfig.setName(name.getText());
        FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
        ftpArgSaved.getFtpConfigArg().put(name.getText(), new FtpConfigArg());
        FtpConfig.saveFavorites(ftpArgSaved);
        stageManager.closeDialog();
    }
}
