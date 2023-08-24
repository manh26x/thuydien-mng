package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class ApiFieldConfig  implements Initializable {
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
    ApiConfig apiConfig;
    ObservableList<Data> dataObservable = FXCollections.observableArrayList();;
    private void setColProperties() {
//        TimingModbusController.generateTableColumnModbus(colKey, colDvt, colNguon, colTenChiTieu, colAddress, colQuantity, colMaThongSo);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColProperties();
        loadDataList();
        apiConfig = SystemArg.API_LIST.stream().filter(e-> e.getName().equals(SystemArg.NAME_API_CHOSEN)).findFirst().get();
        log.info("FOUNDED apiConfig {}", apiConfig);
        int size = dataTable.getItems().size();
        for(int i = 0; i < size && apiConfig.getKeySends() != null ; i++) {
            if(apiConfig.getKeySends().contains(dataTable.getItems().get(i))) {
                dataTable.getSelectionModel().select(i);
            }
        }
    }
    private void loadDataList() {
        dataObservable.clear();
//        dataObservable.addAll(SystemArg.DATA_LIST);
        dataTable.setItems(dataObservable);
    }

    public void close(ActionEvent event) {
        StageManager.getInstance().closeDialog();
    }

    public void save(ActionEvent event) {
        List<Data> keys = new ArrayList<>(dataTable.getSelectionModel().getSelectedItems());
        apiConfig.setKeySends(keys);
        try {
            DataConfig.saveFavorites(null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
