package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.modbus.master.ModbusMasterArg;
import com.codetreatise.thuydienapp.config.modbus.master.ModbusMasterConfig;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModbusServerConfig implements Initializable {
    public TextField name;
    public TextField port;
    public RadioButton rbReady;
    public RadioButton rbNotReady;
    public ToggleGroup readyGroup;

    private final StageManager stageManager;

    public ModbusServerConfig() {
        this.stageManager = StageManager.getInstance();
    }

    public void close(ActionEvent actionEvent) {
        stageManager.closeDialog();
    }

    public void save(ActionEvent actionEvent) throws IOException {
        ModbusMasterArg modbusMasterArg = ModbusMasterArg.builder()
                .name(name.getText())
                .port(Integer.parseInt(port.getText()))
                .ready(rbReady.isSelected())
                .build();
    ModbusMasterConfig.saveFavorites(modbusMasterArg);
    }

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ModbusMasterArg modbusMasterArg = ModbusMasterConfig.getModbusConfig();
        this.name.setText(modbusMasterArg.getName());
        this.port.setText(modbusMasterArg.getPort().toString());
        this.rbReady.setSelected(modbusMasterArg.getReady());
        this.rbNotReady.setSelected(!modbusMasterArg.getReady());
    }
}
