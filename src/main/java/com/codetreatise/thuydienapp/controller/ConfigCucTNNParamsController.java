package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.model.ParamCucTNN;
import com.codetreatise.thuydienapp.repository.CucTNNRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConfigCucTNNParamsController implements Initializable {
    public TableView<ParamCucTNN> paramTable;
    public TableColumn<ParamCucTNN, String> nameCol;
    public TableColumn<ParamCucTNN, Integer> addressCol;
    public TableColumn<ParamCucTNN, String> thongSoCol;
    public TableColumn<ParamCucTNN, String> tramCol;
    public TableColumn<ParamCucTNN, String> dvtCol;
    public ChoiceBox<ModbusParam> paramsSelection;
    public ObservableList<ParamCucTNN> paramsCucTNN = FXCollections.observableArrayList();
    public ObservableList<ModbusParam> modbusParamList;
    public TextField maTinhTxt;
    public TextField kyHieuCongTrinhTxt;

    public void addParam(ActionEvent event) {
        ModbusParam param = paramsSelection.getSelectionModel().getSelectedItem();
        if (param == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Xin hãy chọn tham số để thêm!", ButtonType.YES);
            alert.showAndWait();
        } else {
            paramsCucTNN.add(ParamCucTNN.builder()
                    .name(param.getName())
                    .address(param.getAddress())
                    .build());
        }
    }

    private void setColumnsProperties() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        thongSoCol.setCellValueFactory(new PropertyValueFactory<>("thongSoDo"));
        tramCol.setCellValueFactory(new PropertyValueFactory<>("kyHieuTram"));
        dvtCol.setCellValueFactory(new PropertyValueFactory<>("dvt"));

        Callback cellFactory =
                p -> new EditingCell();
        thongSoCol.setCellFactory(cellFactory);
        thongSoCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setThongSoDo(t.getNewValue()));

        tramCol.setCellFactory(cellFactory);
        tramCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setKyHieuTram(t.getNewValue()));
        dvtCol.setCellFactory(cellFactory);
        dvtCol.setOnEditCommit(
                t -> (t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setDvt(t.getNewValue()));
    }

    public void delete(ActionEvent event) {
    }

    public void save(ActionEvent event) {
        CucTNNRepository.getInstance().saveInfo(maTinhTxt.getText(), kyHieuCongTrinhTxt.getText());
        CucTNNRepository.getInstance().saveAllParams(paramsCucTNN);
    }

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
        paramTable.setItems(paramsCucTNN);
        paramTable.setEditable(true);
        paramsCucTNN.addAll(CucTNNRepository.getInstance().findAll());
        maTinhTxt.setText(CucTNNRepository.getInstance().getInfo().getMaTinh());
        kyHieuCongTrinhTxt.setText(CucTNNRepository.getInstance().getInfo().getKyHieuCongTrinh());
    }
}
