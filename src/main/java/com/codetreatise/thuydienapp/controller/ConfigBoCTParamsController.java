package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.model.ParamBoCT;
import com.codetreatise.thuydienapp.repository.BoCTRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConfigBoCTParamsController implements Initializable {
    public ChoiceBox<ModbusParam> paramsSelection;

    public ObservableList<ModbusParam> modbusParamList;

    public ObservableList<ParamBoCT> paramBoCTS = FXCollections.observableArrayList();
    public TableView<ParamBoCT> paramTable;
    public TableColumn<ParamBoCT, String> nameCol;
    public TableColumn<ParamBoCT, Integer> addressCol;
    public TableColumn<ParamBoCT, String> keyCol;
    public TableColumn<ParamBoCT, String> maThamSoCol;
    public TableColumn<ParamBoCT, String> tenChiTieuCol;
    public TableColumn<ParamBoCT, String> dvtCol;
    public TableColumn<ParamBoCT, String> nguonCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modbusParamList = FXCollections.observableArrayList();
        try {
            modbusParamList.addAll(SqliteJdbc.getInstance().getModbusParams());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        paramsSelection.setItems(modbusParamList);
        setColumnsProperties();
        paramTable.setItems(paramBoCTS);
        paramTable.setEditable(true);
        paramBoCTS.addAll(BoCTRepository.getInstance().findAll());

    }


    private void setColumnsProperties() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        tenChiTieuCol.setCellValueFactory(new PropertyValueFactory<>("tenChiTieu"));
        dvtCol.setCellValueFactory(new PropertyValueFactory<>("dvt"));
        maThamSoCol.setCellValueFactory(new PropertyValueFactory<>("maThamSo"));
        nguonCol.setCellValueFactory(new PropertyValueFactory<>("nguon"));

        Callback cellFactory =
                p -> new EditingCell();
        keyCol.setCellFactory(cellFactory);
        keyCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setKey(t.getNewValue()));

        tenChiTieuCol.setCellFactory(cellFactory);
        tenChiTieuCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setTenChiTieu(t.getNewValue()));
        dvtCol.setCellFactory(cellFactory);
        dvtCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDvt(t.getNewValue()));
        maThamSoCol.setCellFactory(cellFactory);
        maThamSoCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setMaThamSo(t.getNewValue()));

        nguonCol.setCellFactory(cellFactory);
        nguonCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setNguon(t.getNewValue()));
    }

    public void addParam(ActionEvent actionEvent) {
        ModbusParam param = paramsSelection.getSelectionModel().getSelectedItem();
        if (param == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Xin hãy chọn tham số để thêm!", ButtonType.YES);
            alert.showAndWait();
        } else {
            paramBoCTS.add(ParamBoCT.builder()
                            .name(param.getName())
                            .address(param.getAddress())
                    .build());
        }
    }

    public void save(ActionEvent actionEvent) {
        BoCTRepository.getInstance().saveAll(new ArrayList<>(paramBoCTS));
    }

    public void delete(ActionEvent actionEvent) {
        ParamBoCT param = paramTable.getSelectionModel().getSelectedItem();
        if (param != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xóa " + param.getName() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (param.getKey() != null && BoCTRepository.getInstance().findOne(param.getKey()) != null) {
                    BoCTRepository.getInstance().delete(param);
                }
                paramBoCTS.remove(param);
                paramTable.refresh();
            }
        }

    }

}
