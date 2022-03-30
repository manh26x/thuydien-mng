package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

@Controller
public class AddApiController {
    public TextField name;
    public TextField url;
    @Lazy
    @Autowired
    private StageManager stageManager;
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
