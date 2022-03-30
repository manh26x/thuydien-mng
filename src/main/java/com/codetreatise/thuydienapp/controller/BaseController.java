package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

public class BaseController {

    @Lazy
    @Autowired
    protected StageManager stageManager;

    public MenuBar menuBar;


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
    public void ftpConfig(ActionEvent actionEvent) {
        stageManager.switchScene(FxmlView.FTP_CONFIG);
    }
    @FXML
    public void userMng(ActionEvent event) {
        stageManager.switchScene(FxmlView.USER);
    }
    @FXML
    public void addApi(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.ADD_API);
    }
    @FXML
    public void timeCallApi(ActionEvent event) {
        String apiName = ((MenuItem)event.getTarget()).getText();
        SystemArg.NAME_API_CHOSEN = apiName;
        stageManager.switchScene(FxmlView.API_CONFIG);
    }

    protected void initApiMenuGen() {
        SystemArg.API_LIST.forEach(api -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(api.getName());
            menuItem.setId(String.valueOf(api.getId()));
            menuItem.setOnAction(event -> {
                timeCallApi(event);
            });
            if(!menuBar.getMenus().get(0).getItems().stream().filter(e -> e.getText().equals(api.getName())).findAny().isPresent()) {
                menuBar.getMenus().get(0).getItems().add(menuItem);

            }
        });


    }
}
