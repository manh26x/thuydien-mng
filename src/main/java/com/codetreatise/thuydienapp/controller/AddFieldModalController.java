package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.repository.DataRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddFieldModalController {
    @FXML
    public TextField tenThongSo;
    @FXML
    public TextField dvt;

    @FXML
    public TextField address;

    public Button reset;

    public Button saveUser;
    public Label lbMessage;
    private final StageManager stageManager;
    private final DataRepository dataRepository;

    public AddFieldModalController() {
        this.stageManager = StageManager.getInstance();
        dataRepository = DataRepository.getInstance();
    }

    public void reset(ActionEvent event) {
    }

    public void save(ActionEvent event) {
        if(validation()) {
            ModbusParam data = ModbusParam.builder()
                            .name(tenThongSo.getText().trim())
                            .dvt(dvt.getText().trim())
                            .address(Integer.parseInt(address.getText().trim()))
                            .build();
            int result = SqliteJdbc.getInstance().addModbusParam(data);
            if(result == 1) {
                try {
//                    SystemArg.DATA_LIST.add(data);
//                    DataConfig.saveFavorites(null);
                    stageManager.closeDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                lbMessage.setText("Add field Modbus failed!");
            }


        }
    }

    private boolean validation() {
        String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
        address.setStyle(successStyle);
        String message = "";
        boolean valid = true;

        if(address.getText().trim().equals("") || !address.getText().trim().matches("\\d+")) {
            address.setStyle(errorStyle);
            message += "address is required and only have 0-9!\n";
            valid = false;
        }

        lbMessage.setText(message);
        return valid;
    }

    public void close(ActionEvent event) {
        stageManager.closeDialog();

    }
}
