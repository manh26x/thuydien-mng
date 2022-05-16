package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.repository.DataRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

public class AddFieldModalController {
    @FXML
    public TextField key;
    @FXML
    public TextField tenChiTieu;
    @FXML
    public TextField maThongSo;
    @FXML
    public TextField dvt;

    @FXML
    public TextField nguon;
    @FXML
    public TextField address;
    @FXML
    public TextField quantity;

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
            Data data = Data.builder()
                            .key(key.getText().trim())
                            .tenChiTieu(tenChiTieu.getText().trim())
                            .dvt(dvt.getText().trim())
                            .nguon(nguon.getText().trim())
                            .maThongSo(maThongSo.getText().trim())
                            .address(Integer.parseInt(address.getText().trim()))
                            .quantity(Integer.parseInt(quantity.getText().trim()))
                            .status(1)
                            .build();
            SystemArg.DATA_LIST.add(data);
            dataRepository.insert(data);
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
        key.setStyle(successStyle);
        maThongSo.setStyle(successStyle);
        address.setStyle(successStyle);
        quantity.setStyle(successStyle);
        String message = "";
        boolean valid = true;
        if(key.getText().trim().equals("")) {
            key.setStyle(errorStyle);
            message += "Key is required!\n";
            valid = false;
        }
        if(nguon.getText().trim().equals("")) {
            nguon.setStyle(errorStyle);
            message += "nguon is required!\n";
            valid = false;
        }
        if(maThongSo.getText().trim().equals("")) {
            maThongSo.setStyle(errorStyle);
            message += "Ma thong so is required!\n";
            valid = false;
        }
        if(address.getText().trim().equals("") || !address.getText().trim().matches("\\d+")) {
            address.setStyle(errorStyle);
            message += "address is required and only have 0-9!\n";
            valid = false;
        }
        if(quantity.getText().trim().equals("") || !quantity.getText().trim().matches("\\d+")) {
            quantity.setStyle(errorStyle);
            message += "quantity is required and only have 0-9!\n";
            valid = false;
        }
        lbMessage.setText(message);
        return valid;
    }

    public void close(ActionEvent event) {
        stageManager.closeDialog();

    }
}
