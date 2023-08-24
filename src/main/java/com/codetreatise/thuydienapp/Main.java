package com.codetreatise.thuydienapp;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.config.*;
import com.codetreatise.thuydienapp.config.database.InitDatabase;
import com.codetreatise.thuydienapp.config.modbus.master.ModbusMasterStart;
import com.codetreatise.thuydienapp.config.modbus.slave.ModbusSchedule;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.Timer;


public class Main extends Application {

    protected StageManager stageManager;

    public static void main(final String[] args) {
        System.setProperty("java.awt.headless", "false");
        Application.launch(args);
    }



    @Override
    public void start(Stage stage) throws Exception {
        try {
            DataConfig.getHostsList();
            if (SystemArg.API_LIST.stream().noneMatch(a -> a.getName().equals(Constants.BO_CT_NAME))) {
                ApiConfig apiConfig = new ApiConfig();
                apiConfig.setName(Constants.BO_CT_NAME);
                apiConfig.setUrl(Constants.BO_CT_URL);
                SystemArg.API_LIST.add(apiConfig);
            }
            if (SystemArg.API_LIST.stream().noneMatch(a -> a.getName().equals(Constants.CUC_TNN_NAME))) {
                ApiConfig apiConfig = new ApiConfig();
                apiConfig.setName(Constants.CUC_TNN_NAME);
                apiConfig.setUrl(Constants.CUC_TNN_URL);
                SystemArg.API_LIST.add(apiConfig);
            }
            DataConfig.saveFavorites(null);
        } catch (Exception ignored) {
        }
        stageManager = StageManager.getInstance();
        SpringFXMLLoader springFXMLLoader = SpringFXMLLoader.getInstance();
        springFXMLLoader.setResourceBundle(ResourceBundle.getBundle("Bundle"));
        stageManager.setArgs(springFXMLLoader, stage);
        displayInitialScene();
        stageManager.init();
        stageManager.setup();


        Timer timer = new Timer();
        timer.schedule(InitDatabase.getInstance(),0);
        ModbusMasterStart.getInstance().reloadModbus();
        timer.scheduleAtFixedRate(ModbusSchedule.getInstance(), 2000, 10000);
        timer.scheduleAtFixedRate(SynchronizeConfig.getInstance(), 2000, 10000);

    }

    @Override
    public void stop() throws Exception {

    }

    /**
     * Useful to override this method by sub-classes wishing to change the first
     * Scene to be displayed on startup. Example: Functional tests on main
     * window.
     */
    protected void displayInitialScene() {
        stageManager.switchScene(FxmlView.TIMING_MODBUS);
    }

}
