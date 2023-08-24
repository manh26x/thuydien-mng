package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.repository.DataRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateFieldModalController implements Initializable {
    @FXML
    public TextField tenThamSo;
    @FXML
    public TextField dvt;

    @FXML
    public TextField address;

    public Button reset;

    public Button saveUser;
    public Label lbMessage;
    public static ModbusParam DATA_CHOSEN = new ModbusParam();
    private final StageManager stageManager;

    private final DataRepository dataRepository;

    public UpdateFieldModalController() {
        dataRepository = DataRepository.getInstance();
        stageManager = StageManager.getInstance();
    }

    public void reset(ActionEvent event) {
    }

    public void save(ActionEvent event) {
        if(validation()) {

            dataRepository.update(ModbusParam.builder()
                            .name(tenThamSo.getText())
                            .address(Integer.parseInt(address.getText()))
                            .dvt(dvt.getText())
                    .build());
            try {
                DataConfig.saveFavorites(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stageManager.closeDialog();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.address.setText("" + DATA_CHOSEN.getAddress());
        this.dvt.setText(DATA_CHOSEN.getDvt());
        this.tenThamSo.setText(DATA_CHOSEN.getName());
        this.tenThamSo.setDisable(true);
    }
}
