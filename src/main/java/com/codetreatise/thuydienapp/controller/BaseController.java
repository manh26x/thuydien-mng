package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

public class BaseController {

    @Lazy
    @Autowired
    protected StageManager stageManager;

    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void timeSyncModbus(ActionEvent event) {
        stageManager.switchScene(FxmlView.TIMING_MODBUS);
    }
    /**
     * Logout and go to the login page
     */
    @FXML
    private void logout(ActionEvent event) throws IOException {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    @FXML
    public void userMng(ActionEvent event) {
        stageManager.switchScene(FxmlView.USER);
    }

    @FXML
    public void timeCallApi(ActionEvent event) {
        stageManager.switchScene(FxmlView.API_CONFIG);
    }
}
