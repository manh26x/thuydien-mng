package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class AddApiController {
    public TextField name;
    public TextField url;
    private final StageManager stageManager;

    public AddApiController() {
        stageManager = StageManager.getInstance();
    }

    public void close(ActionEvent actionEvent) {
        stageManager.closeDialog();
    }

    public void save(ActionEvent actionEvent) {
        ApiConfig apiConfig =new ApiConfig();
        apiConfig.setName(name.getText());
        apiConfig.setUrl(url.getText());
        SystemArg.API_LIST.add(apiConfig);
        try {
            DataConfig.saveFavorites(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stageManager.closeDialog();
    }



}
