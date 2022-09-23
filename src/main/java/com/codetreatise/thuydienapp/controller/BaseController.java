package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.DataError;
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
import javafx.event.Event;
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
import java.util.stream.Collectors;

public abstract class BaseController implements Observer {


    protected final StageManager stageManager;


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
        SystemArg.NAME_API_CHOSEN = ((MenuItem)event.getTarget()).getText();
        stageManager.switchScene(FxmlView.API_CONFIG);
    }

    @FXML
    public void modbusServerConfig(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.MODBUS_SERVER_CONFIG);
    }
    public void gotoModbusError(ActionEvent actionEvent) {
        SystemArg.ERROR_TYPE_CHOSEN = Constants.MODBUS_TYPE;
        SystemArg.MENU_ERROR_CHOSEN = "Modbus";
        stageManager.switchScene(FxmlView.DATA_ERROR);
    }
    public void initApiMenuGen() {
        try {
            errorMenu.setStyle(SystemArg.getStatusMenuStyle("Modbus"));
            FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
            if(menuBar.getMenus().get(0).getItems().stream()
                    .anyMatch(item -> "FTP".equals(item.getUserData()))) {
                menuBar.getMenus().removeAll(menuBar.getMenus().stream().filter(item -> "FTP".equals(item.getUserData()))
                        .collect(Collectors.toList()));
                menuBar.getMenus().get(0).getItems().removeAll(
                        menuBar.getMenus().get(0).getItems().stream()
                                .filter(item -> "FTP".equals(item.getUserData())).collect(Collectors.toList()));

            }

            ftpArgSaved.getFtpConfigArg().keySet().forEach(ftpName -> {
                MenuItem menuItem = new MenuItem();
                menuItem.setText(ftpName);
                menuItem.setId(ftpName);
                menuItem.setUserData("FTP");
                menuItem.setOnAction(this::ftpConfig);

                if(menuBar.getMenus().get(0).getItems().stream().noneMatch(e -> e.getText().equals(ftpName))) {
                    menuBar.getMenus().get(0).getItems().add(menuItem);
                }
                if(menuBar.getMenus().stream().noneMatch(e -> e.getText().equals("FTP: " + ftpName))) {
                    Menu menu = new Menu();
                    menu.setText("FTP: " + ftpName);
                    menu.setStyle(SystemArg.getStatusMenuStyle("FTP: " + ftpName));
                    menu.setUserData("FTP");
                    menuItem = new MenuItem("View");
                    menuItem.setOnAction(e -> {
                        SystemArg.ERROR_TYPE_CHOSEN = Constants.FTP_TYPE;
                        SystemArg.MENU_ERROR_CHOSEN =  ftpName;
                        stageManager.switchScene(FxmlView.DATA_ERROR);
                    });
                    menu.getItems().add(menuItem);
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
                menu.setStyle(SystemArg.getStatusMenuStyle("API: " + api.getName()));
                menuItem = new MenuItem("View");
                menuItem.setOnAction(e -> {
                    SystemArg.ERROR_TYPE_CHOSEN = Constants.API_TYPE;
                    SystemArg.MENU_ERROR_CHOSEN =  api.getName();
                    stageManager.switchScene(FxmlView.DATA_ERROR);
                });
                menu.getItems().add(menuItem);
                menuBar.getMenus().add(menu);
            }
        });


    }

    public void addFtp(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.ADD_FTP);
    }

    @FXML
    public void showErrorData(Event actionEvent) {
        stageManager.switchScene(FxmlView.DATA_ERROR);
    }
    @Override
    public final void update(Observable o, Object arg) {
        try {
            reload();
        } catch (Exception ignored) {

        }
        if(arg == null) {
            initApiMenuGen();
        } else if(arg instanceof EventObject) {
            EventObject event = (EventObject) arg;
            DataError dataError = event.getDataError();
            String menuName = dataError.getMenuName();
            if(event.getType().equals(Constants.CONST_ERROR)) {
                setStyleMenu(dataError, menuName, SystemArg.ERROR_STATUS_STYLE);
                DataErrorRepository.getInstance().insert(dataError);
            } else {
                setStyleMenu(dataError, menuName, SystemArg.OKE_STATUS_STYLE);
            }
        }
    }

    private void setStyleMenu(DataError dataError, String menuName, String statusStyle) {
        if(dataError.getType().equals(Constants.API_TYPE)) {
            Optional<Menu> menuError = menuBar.getMenus().stream().filter(menu -> (menu.getText()).equals("API: " + menuName)).findFirst();
            menuError.ifPresent(menu -> {
                menu.setStyle(statusStyle);
                SystemArg.mapErrorMenu.put(menu.getText(), statusStyle);
            });
        } else if(dataError.getType().equals(Constants.MODBUS_TYPE)) {
            Optional<Menu> menuError = menuBar.getMenus().stream().filter(menu -> menu.getText().equals(menuName)).findFirst();
            menuError.ifPresent(menu -> {
                SystemArg.mapErrorMenu.put(menu.getText(), statusStyle);
                menu.setStyle(statusStyle);
                menu.setOnAction(e -> stageManager.switchScene(FxmlView.DATA_ERROR));
            });
        } else if(dataError.getType().equals(Constants.FTP_TYPE)) {
            Optional<Menu> menuError = menuBar.getMenus().stream().filter(menu -> menu.getText().equals("FTP: " + menuName)).findFirst();
            menuError.ifPresent(menu -> {
                SystemArg.mapErrorMenu.put(menu.getText(), statusStyle);
                menu.setStyle(statusStyle);
                menu.setOnAction(e -> stageManager.switchScene(FxmlView.DATA_ERROR));
            });
        }
    }
    protected abstract void reload();
}
