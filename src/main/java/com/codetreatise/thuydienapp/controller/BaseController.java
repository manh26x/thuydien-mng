package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.event.ErrorTrigger;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.repository.DataErrorRepository;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class BaseController implements Observer {


    protected final StageManager stageManager;
    private static final String OKE_STATUS_STYLE = "-fx-background-color: green";
    private static final String ERROR_STATUS_STYLE = "-fx-background-color: red";

    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu errorMenu;

    public BaseController() {
        this.stageManager = StageManager.getInstance();
        EventTrigger.getInstance().deleteObservers();
        EventTrigger.getInstance().addObserver(this);
        ErrorTrigger.getInstance().deleteObservers();
        ErrorTrigger.getInstance().addObserver(this);
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
            ftpArgSaved.getFtpConfigArg().keySet().forEach(ftpName -> {
                MenuItem menuItem = new MenuItem();
                menuItem.setText(ftpName);
                menuItem.setId(ftpName);
                menuItem.setOnAction(this::ftpConfig);
                if(menuBar.getMenus().get(0).getItems().stream().noneMatch(e -> e.getText().equals(ftpName))) {
                    menuBar.getMenus().get(0).getItems().add(menuItem);
                }
                if(menuBar.getMenus().stream().noneMatch(e -> e.getText().equals("FTP: " + ftpName))) {
                    Menu menu = new Menu();
                    menu.setText("FTP: " + ftpName);
                    menu.setStyle(OKE_STATUS_STYLE);
                    menuBar.getMenus().add(menu);
                }
            });

        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | NullPointerException e) {
            e.printStackTrace();
        }
        SystemArg.API_LIST.forEach(api -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(api.getName());
            menuItem.setId(String.valueOf(api.getId()));
            menuItem.setOnAction(this::timeCallApi);
            if(menuBar.getMenus().get(0).getItems().stream().noneMatch(e -> e.getText().equals(api.getName()))) {
                menuBar.getMenus().get(0).getItems().add(menuItem);
            }
            if(menuBar.getMenus().stream().noneMatch(e -> e.getText().equals("API: " + api.getName()))) {
                Menu menu = new Menu();
                menu.setText("API: " + api.getName());
                menu.setStyle(OKE_STATUS_STYLE);
                menuBar.getMenus().add(menu);
            }
        });


    }

    @Override
    public final void update(Observable o, Object arg) {
        if(arg == null) {
            initApiMenuGen();
        } else if(arg instanceof EventObject) {
            EventObject event = (EventObject) arg;
            DataError dataError = event.getDataError();
            String menuName = dataError.getMenuName();
            if(event.getType().equals(Constants.CONST_ERROR)) {
                setStyleMenu(dataError, menuName, ERROR_STATUS_STYLE);
            } else {
                setStyleMenu(dataError, menuName, OKE_STATUS_STYLE);
            }
        }
    }

    private void setStyleMenu(DataError dataError, String menuName, String statusStyle) {
        if(dataError.getType().equals(Constants.API_TYPE)) {
            Optional<Menu> menuError = menuBar.getMenus().stream().filter(menu -> (menu.getText()).equals("API: " + menuName)).findFirst();
            menuError.ifPresent(menu -> menu.setStyle(statusStyle));
        } else if(dataError.getType().equals(Constants.MODBUS_TYPE)) {
            Optional<Menu> menuError = menuBar.getMenus().stream().filter(menu -> menu.getText().equals(menuName)).findFirst();
            menuError.ifPresent(menu -> menu.setStyle(statusStyle));
        }
    }
}
