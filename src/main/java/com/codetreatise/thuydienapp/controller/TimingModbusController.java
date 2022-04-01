package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TimingModbusController extends BaseController implements Initializable {

    @FXML
    public TextField modbusIP;
    @FXML
    public TextField modbusPort;
    @FXML
    public RadioButton rbReady;
    @FXML
    public ToggleGroup readyGroup;
    @FXML
    public RadioButton rbNotReady;

    @FXML
    public Button reset;
    @FXML
    public Button save;
    @FXML
    public TableView dataTable;
    @FXML
    public TableColumn colKey;
    @FXML
    public TableColumn colNguon;
    @FXML
    public TableColumn colTenChiTieu;
    @FXML
    public TableColumn colDvt;
    @FXML
    public TableColumn colAddress;
    @FXML
    public TableColumn colQuantity;
    @FXML
    public TableColumn colMaThongSo;
    @FXML
    public TableColumn colTrangThai;
    @FXML
    public Label lbMessage;
    @FXML
    public ComboBox timeChosen;
    @FXML
    public TextField slaveId;



    ObservableList<Data> dataObservable = FXCollections.observableArrayList();;


    private void loadDataList() {
        dataObservable.clear();
        dataObservable.addAll(SystemArg.DATA_LIST);
        dataTable.setItems(dataObservable);
    }
    private void setColProperties() {
        colKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        colDvt.setCellValueFactory(new PropertyValueFactory<>("dvt"));
        colNguon.setCellValueFactory(new PropertyValueFactory<>("nguon"));
        colTenChiTieu.setCellValueFactory(new PropertyValueFactory<>("tenChiTieu"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMaThongSo.setCellValueFactory(new PropertyValueFactory<>("maThongSo"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int[] timeChosenList = new int[] { 5, 10, 15, 30, 60 };
        timeChosen.getItems().addAll(Arrays.stream(timeChosenList)
                .boxed()
                .collect(Collectors.toList()));
        reset(null);
        setColProperties();
        loadDataList();
        super.initApiMenuGen();
    }



    @FXML
    public void save(ActionEvent event) {
        if(isValid()) {
            SystemArg.MODBUS_IP = modbusIP.getText().trim();
            SystemArg.MODBUS_PORT = Integer.parseInt(modbusPort.getText());
            SystemArg.UNIT = (byte) Integer.parseInt(slaveId.getText());
            SystemArg.TIME_SCHEDULE_SYNC_MODBUS = (Integer) timeChosen.getSelectionModel().getSelectedItem() * 60 * 1000;
            SystemArg.MODBUS_SYNC_READY = rbReady.isSelected();
            SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date();
            try {
                DataConfig.saveFavorites(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void resfresh(ActionEvent event) {
        initialize(null, null);
    }

    private boolean isValid() {
        boolean valid = true;
        String message = "";
        String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
        modbusPort.setStyle(successStyle);
        modbusIP.setStyle(successStyle);
        slaveId.setStyle(successStyle);
        if(modbusIP.getText().trim().equals("")) {
            message += "Modbus IP is required!\n";
            modbusIP.setStyle(errorStyle);
            valid = false;
        }
        if(modbusPort.getText().trim().equals("") || !modbusPort.getText().trim().matches("\\d+")) {
            message += "Modbus Port is required and it must be a digit 0-9\n";
            modbusPort.setStyle(errorStyle);
            valid = false;
        }
        if(slaveId.getText().trim().equals("") || !slaveId.getText().trim().matches("\\d+")) {
            message += "Slave ID is required and it must be a digit 0-9\n";
            slaveId.setStyle(errorStyle);
            valid = false;
        }
        if(timeChosen.getSelectionModel().isEmpty()) {
            message += "Time Sync Modbus must be chosen\n";
            timeChosen.setStyle(errorStyle);
            valid = false;
        }
        lbMessage.setText(message);
        return valid;
    }



    public void addData(ActionEvent event) {
        stageManager.createModal(FxmlView.ADD_FIELD_MODAL);
    }



    public void reset(ActionEvent event) {
        modbusIP.setText(SystemArg.MODBUS_IP);
        modbusPort.setText(SystemArg.MODBUS_PORT.toString());
        slaveId.setText(SystemArg.UNIT.toString());
        switch (SystemArg.TIME_SCHEDULE_SYNC_MODBUS / (60 * 1000)) {
            case 5:
                timeChosen.getSelectionModel().select(0);
                break;
            case 10:
                timeChosen.getSelectionModel().select(1);
                break;
            case 15:
                timeChosen.getSelectionModel().select(2);
                break;
            case 30:
                timeChosen.getSelectionModel().select(3);
                break;
            case 60:
                timeChosen.getSelectionModel().select(4);
                break;
        }

        rbReady.setSelected(SystemArg.MODBUS_SYNC_READY);
        rbNotReady.setSelected(!SystemArg.MODBUS_SYNC_READY);
    }

    public void delete(ActionEvent event) {
        SystemArg.DATA_LIST.remove(dataTable.getSelectionModel().getSelectedItem());
        dataObservable.remove(dataTable.getSelectionModel().getSelectedItem());
        dataTable.setItems(dataObservable);
        try {
            DataConfig.saveFavorites(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateData(ActionEvent actionEvent) {
        UpdateFieldModalController.DATA_CHOSEN = (Data) dataTable.getSelectionModel().getSelectedItem();
        stageManager.createModal(FxmlView.UPDATE_FIELD_MODAL);

    }

    public void addFtp(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.ADD_FTP);
    }
}
