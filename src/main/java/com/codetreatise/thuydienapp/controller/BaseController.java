package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class BaseController implements Observer {


    protected final StageManager stageManager;

    public MenuBar menuBar;

    public BaseController() {
        this.stageManager = StageManager.getInstance();
        EventTrigger.getInstance().deleteObservers();
        EventTrigger.getInstance().addObserver(this);
    }


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
        String ftpName = ((MenuItem)actionEvent.getTarget()).getText();
        SystemArg.NAME_FTP_CHOSEN = ftpName;
        stageManager.switchScene(FxmlView.FTP_CONFIG);
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

    @FXML
    public void modbusServerConfig(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.MODBUS_SERVER_CONFIG);
    }

    protected void initApiMenuGen() {

        try {
            FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
            ftpArgSaved.getFtpConfigArg().keySet().stream().forEach(ftpName -> {
                MenuItem menuItem = new MenuItem();
                menuItem.setText(ftpName);
                menuItem.setId(ftpName);
                menuItem.setOnAction(event -> {
                    ftpConfig(event);
                });
                if(!menuBar.getMenus().get(0).getItems().stream().filter(e -> e.getText().equals(ftpName)).findAny().isPresent()) {
                    menuBar.getMenus().get(0).getItems().add(menuItem);
                }
            });
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | NullPointerException e) {
            e.printStackTrace();
        }
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

    @Override
    public final void update(Observable o, Object arg) {
        System.out.println("changed " + arg);
        initApiMenuGen();
    }
}
