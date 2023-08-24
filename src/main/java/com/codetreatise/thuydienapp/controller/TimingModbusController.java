package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.*;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.config.modbus.slave.ModbusClientGetData;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.model.ModbusDataModel;
import com.codetreatise.thuydienapp.model.ModbusParamData;
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
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
    public TableView<ModbusParamData> dataTable;
    @FXML
    public TableColumn colTenChiTieu;
    @FXML
    public TableColumn colDvt;
    @FXML
    public TableColumn colAddress;
    @FXML
    public TableColumn colCurrentValue;
    @FXML
    public Label lbMessage;
    @FXML
    public ComboBox timeChosen;
    @FXML
    public TextField slaveId;
    public TableView modbusData;
    public TableColumn modTime;

    private TimerTask updateModbusThread;
    private Timer timer;

    @FXML
    public DatePicker dateSearch;


    ObservableList<ModbusParamData> dataObservable = FXCollections.observableArrayList();;
    ObservableList<ModbusDataModel> receiveObservableList = FXCollections.observableArrayList();

    private DataReceiveJdbc receiveJdbc;


    private void loadDataList() {
        dataObservable.clear();
        // TODO Generate data
        try {
            List<ModbusParamData> datas = new ArrayList<>(SqliteJdbc.getInstance().getModbusParams().stream().map(ModbusParamData::new).collect(Collectors.toList()));
            dataObservable.addAll(datas);
            this.dataTable.setItems(dataObservable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadModbusData() throws SQLException {
        receiveObservableList.clear();
        Map<Date, ModbusDataModel> modbusDataMap = new HashMap<>();
        DataRepository.getInstance().findAllModbusDataByDate(convertToDate(dateSearch)).stream().forEach(e -> {
            ModbusDataModel dataModel = modbusDataMap.get(e.getTimeReceive());
            if (dataModel == null) {
                dataModel = new ModbusDataModel();
                dataModel.setModTime(e.getTimeReceive());
                modbusDataMap.put(e.getTimeReceive(), dataModel);
            }
            dataModel.getData().put(e.getName(), Math.round(e.getValue() * 100) / 100f);
        });
        receiveObservableList.addAll(modbusDataMap.values());
        modbusData.setItems(receiveObservableList);
    }

    private Date convertToDate(DatePicker picker) {
        LocalDate localDate = picker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
    }

    private void setColProperties() {
        colTenChiTieu.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDvt.setCellValueFactory(new PropertyValueFactory<>("dvt"));
        colCurrentValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        modTime.setCellValueFactory(new PropertyValueFactory<>("modTime"));
        try {
            SqliteJdbc.getInstance().getModbusParams().stream().forEach(e -> {
                TableColumn modCol = new TableColumn(e.getName());
                modCol.setCellValueFactory(new PropertyValueFactory<>("data"));
                modCol.setCellFactory(x -> new TableCell<ObservableList<String>, Map<String, Float>>() {
                    @Override
                    public void updateItem(Map<String, Float> item,  boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText("-");
                        } else if(item.get(e.getName()) != null) {
                            setText(item.get(e.getName()).toString());
                        } else {
                            setText("-");
                        }

                    }
                });
                modbusData.getColumns().add(modCol);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int[] timeChosenList = new int[] { 5, 10, 15, 30, 60 };
        timeChosen.getItems().addAll(Arrays.stream(timeChosenList)
                .boxed()
                .collect(Collectors.toList()));
        if (dateSearch.getValue() == null) {
            dateSearch.setValue(LocalDate.now());
        }
        reset(null);
        setColProperties();
        loadDataList();
        try {
            loadModbusData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.initApiMenuGen();
    }

    public void searchDataByDate(Date date) {

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
        try {
            loadModbusData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String name = dataTable.getSelectionModel().getSelectedItem().getName();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xoá " + name + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        try {
            if (alert.getResult() == ButtonType.YES) {
                DataRepository.getInstance().deleteModbusParam(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateData(ActionEvent actionEvent) {
        UpdateFieldModalController.DATA_CHOSEN = dataTable.getSelectionModel().getSelectedItem();
        stageManager.createModal(FxmlView.UPDATE_FIELD_MODAL);

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
//        TODO update the value in param table
        if(ModbusClientGetData.getInstance().isConnected()) {
            try {
                for (ModbusParamData param: dataObservable) {
                    int address = param.getAddress();
                    float arg = ModbusClientGetData.getInstance().getValue(address, 2);
                    param.setValue(arg);
                }
            } catch (ModbusException | IOException e) {
                e.printStackTrace();
            }
        }
        dataTable.refresh();

    }

    public void disconnect(ActionEvent event) throws IOException {
        ModbusClientGetData.getInstance().disconnect();
        updateModbusThread = null;
        timer.cancel();

    }

    public void resfreshModbus(ActionEvent event) {
        updateDataModbus();
    }




    public void importFileExcel(ActionEvent event) {
        stageManager.createModal(FxmlView.FILE_EXCEL_IMPORT);
    }
    @Override
    protected void reload() {
        loadDataList();
    }



}
