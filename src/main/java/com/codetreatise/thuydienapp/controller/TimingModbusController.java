package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.bean.ModbusDataReceiveTable;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.modbus.slave.ModbusClientGetData;
import com.codetreatise.thuydienapp.config.modbus.slave.ModbusDataReceive;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.repository.DataReceiveJdbc;
import com.codetreatise.thuydienapp.repository.DataRepository;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import com.codetreatise.thuydienapp.view.FxmlView;
import de.re.easymodbus.exceptions.ModbusException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    public TableView<Data> dataTable;
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
    public TableColumn<Object, Object> colTrangThai;
    @FXML
    public Label lbMessage;
    @FXML
    public ComboBox timeChosen;
    @FXML
    public TextField slaveId;
    public TableView<ModbusDataReceive> modbusTable;
    public TableColumn colModAddress;
    public TableColumn colModQuantity;
    public TableColumn colModValue;
    public TextField address;
    public TextField quantity;
    public TextField sizeMod;
    public TableColumn modTenChiTieu;
    public TableColumn modAddress;
    public TableColumn modMaThongSo;
    public TableColumn modValue;
    public TableView<ModbusDataReceiveTable> modbusData;
    public TableColumn modTime;

    private TimerTask updateModbusThread;
    private Timer timer;


    ObservableList<Data> dataObservable = FXCollections.observableArrayList();;
    ObservableList<ModbusDataReceive> receiveObservableList = FXCollections.observableArrayList();
    ObservableList<ModbusDataReceiveTable> modbusDataReceives = FXCollections.observableArrayList();

    private DataReceiveJdbc receiveJdbc;


    private void loadModbusData() {
        try {
            receiveJdbc = DataReceiveJdbc.getInstance();
            modbusDataReceives.clear();
            modbusDataReceives.addAll(receiveJdbc.findAllByTime(null, null));
            modbusData.setItems(modbusDataReceives);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataList() {
        dataObservable.clear();
        dataObservable.addAll(SystemArg.DATA_LIST);
        dataTable.setItems(dataObservable);
    }
    private void setColProperties() {
        modTenChiTieu.setCellValueFactory(new PropertyValueFactory<>("tenChiTieu"));
        modAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        modValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        modTime.setCellValueFactory(new PropertyValueFactory<>("timeString"));
        modMaThongSo.setCellValueFactory(new PropertyValueFactory<>("maThongSo"));

        colModAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colModQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colModValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        generateTableColumnModbus(colKey, colDvt, colNguon, colTenChiTieu, colAddress, colQuantity, colMaThongSo);
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    static void generateTableColumnModbus(TableColumn colKey, TableColumn colDvt, TableColumn colNguon, TableColumn colTenChiTieu, TableColumn colAddress, TableColumn colQuantity, TableColumn colMaThongSo) {
        colKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        colDvt.setCellValueFactory(new PropertyValueFactory<>("dvt"));
        colNguon.setCellValueFactory(new PropertyValueFactory<>("nguon"));
        colTenChiTieu.setCellValueFactory(new PropertyValueFactory<>("tenChiTieu"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMaThongSo.setCellValueFactory(new PropertyValueFactory<>("maThongSo"));
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
        loadModbusData();
        super.initApiMenuGen();
    }



    @FXML
    public void save(ActionEvent event) {
        if(isValid()) {
            SystemArg.MODBUS_IP = modbusIP.getText().trim();
            SystemArg.MODBUS_PORT = Integer.parseInt(modbusPort.getText());
            SystemArg.UNIT = (byte) Integer.parseInt(slaveId.getText());
            SystemArg.TIME_SCHEDULE_SYNC_MODBUS = (Integer) timeChosen.getSelectionModel().getSelectedItem() ;
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

        switch (SystemArg.TIME_SCHEDULE_SYNC_MODBUS) {
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
        int result = DataRepository.getInstance().delete(dataTable.getSelectionModel().getSelectedItem());
        if(result == 1) {
            dataObservable.remove(dataTable.getSelectionModel().getSelectedItem());
            dataTable.setItems(dataObservable);
            try {
                DataConfig.saveFavorites(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void updateData(ActionEvent actionEvent) {
        UpdateFieldModalController.DATA_CHOSEN = dataTable.getSelectionModel().getSelectedItem();
        stageManager.createModal(FxmlView.UPDATE_FIELD_MODAL);

    }

    public void addFtp(ActionEvent actionEvent) {
        stageManager.createModal(FxmlView.ADD_FTP);
    }


    public void connect(ActionEvent event) {
        try {
            getDataModbus();
        } catch (IOException e) {
            e.printStackTrace();
            EventTrigger.getInstance().setChange();
            EventTrigger.getInstance().notifyObservers(EventObject.builder()
                    .type(Constants.CONST_ERROR)
                    .dataError(DataError.builder()
                            .type(Constants.MODBUS_TYPE)
                            .menuName("Modbus")
                            .build())
                    .build());
        }
    }

    private void getDataModbus() throws IOException {
        ModbusClientGetData.getInstance().connect(
                    this.modbusIP.getText(),
                    Integer.parseInt(this.modbusPort.getText().trim()),
                    (byte) Integer.parseInt(slaveId.getText()));
        if(this.updateModbusThread == null) {
            updateModbusThread = new TimerTask() {
                @Override
                public void run() {
                    updateDataModbus();
                }
            };
            timer = new Timer();
            timer.scheduleAtFixedRate(updateModbusThread, 0, 1000);
        }
    }

    private void updateDataModbus() {

        if(ModbusClientGetData.getInstance().isConnected()) {
            try {
                Integer sizeMod = Integer.parseInt(this.sizeMod.getText().trim());
                Integer address = Integer.parseInt(this.address.getText().trim());
                Integer quantity = Integer.parseInt(this.quantity.getText().trim());
                for(int i = 0; i < sizeMod; i++) {
                    float arg = ModbusClientGetData.getInstance().getValue(address + i*quantity, quantity);
                    ModbusDataReceive dataReceive = null;
                    try {
                        dataReceive = receiveObservableList.get(i);
                    } catch (Exception ignored) {}
                    if(dataReceive == null) {
                        receiveObservableList.add(ModbusDataReceive.builder()
                                .address(String.valueOf(address + i*quantity))
                                .quantity(String.valueOf(quantity))
                                .value(arg)
                                .build());
                    } else if(dataReceive.getValue() != arg) {
                        dataReceive.setValue(arg);
                    }

                }
            } catch (ModbusException | IOException e) {
                e.printStackTrace();
            }
        } else {
            receiveObservableList.clear();
        }
        modbusTable.setItems(receiveObservableList);
        modbusTable.refresh();

    }

    public void disconnect(ActionEvent event) throws IOException {
        ModbusClientGetData.getInstance().disconnect();
        updateModbusThread = null;
        timer.cancel();

    }

    public void resfreshModbus(ActionEvent event) {
        updateDataModbus();
    }


    public void resfreshModbusData(ActionEvent event) {
        loadModbusData();
    }
}
