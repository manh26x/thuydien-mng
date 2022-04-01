package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.config.ftp.FtpConfigArg;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

@Controller
public class AddFtpController {
    public TextField name;

    @Lazy
    @Autowired
    private StageManager stageManager;
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
