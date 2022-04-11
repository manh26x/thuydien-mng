package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.repository.DataRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateFieldModalController implements Initializable {
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
    public static Data DATA_CHOSEN = new Data();
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
            DATA_CHOSEN.setKey(key.getText().trim());
            DATA_CHOSEN.setAddress(Integer.parseInt(address.getText().trim()));
            DATA_CHOSEN.setDvt(dvt.getText().trim());
            DATA_CHOSEN.setTenChiTieu(tenChiTieu.getText().trim());
            DATA_CHOSEN.setMaThongSo(maThongSo.getText().trim());
            DATA_CHOSEN.setNguon(nguon.getText().trim());
            DATA_CHOSEN.setQuantity(Integer.parseInt(quantity.getText().trim()));
            dataRepository.update(DATA_CHOSEN);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.address.setText(DATA_CHOSEN.getAddress().toString());
        this.quantity.setText(DATA_CHOSEN.getQuantity().toString());
        this.key.setText(DATA_CHOSEN.getKey());
        this.maThongSo.setText(DATA_CHOSEN.getMaThongSo());
        this.dvt.setText(DATA_CHOSEN.getDvt());
        this.tenChiTieu.setText(DATA_CHOSEN.getTenChiTieu());
        this.nguon.setText(DATA_CHOSEN.getNguon());
    }
}
