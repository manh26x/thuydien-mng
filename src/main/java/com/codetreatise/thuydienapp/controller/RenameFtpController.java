package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.config.ftp.FtpConfigArg;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.Observable;

public class RenameFtpController extends Observable {
    public TextField name;
    private final StageManager stageManager;

    public RenameFtpController() {
        stageManager = StageManager.getInstance();
    }

    public void close(ActionEvent actionEvent) {
        stageManager.closeDialog();
    }

    public void save(ActionEvent actionEvent) throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
        FtpConfigArg ftpConfigArg = ftpArgSaved.getFtpConfigArg().get(SystemArg.NAME_FTP_CHOSEN);
        ftpConfigArg.setMenuName(name.getText());
        ftpArgSaved.getFtpConfigArg().put(name.getText(), ftpConfigArg);
        ftpArgSaved.getFtpConfigArg().remove(SystemArg.NAME_FTP_CHOSEN);
        SystemArg.NAME_FTP_CHOSEN = name.getText();
        FtpConfig.saveFavorites(ftpArgSaved);
        super.notifyObservers("rename ftp");
        super.setChanged();
        stageManager.closeDialog();
    }
}
