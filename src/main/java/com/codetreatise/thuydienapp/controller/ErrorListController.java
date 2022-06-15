package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.repository.DataErrorRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ErrorListController extends BaseController  implements Initializable {

    private final DataErrorRepository errorRepository;
    public TableView<DataError> table;
    public TableColumn<Object, Object> colTime;
    public TableColumn<Object, Object> colTitle;
    public TableColumn<Object, Object> colMessage;
    public TableColumn colAction;

    public ObservableList<DataError> dataErrors = FXCollections.observableArrayList();

    public ErrorListController() {
        this.errorRepository = DataErrorRepository.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initApiMenuGen();
        colTime.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        reloadTable();
    }

    private void reloadTable() {
        dataErrors.clear();
        dataErrors.addAll(errorRepository.getAllUnReadByTypeAndMenu(SystemArg.ERROR_TYPE_CHOSEN, SystemArg.MENU_ERROR_CHOSEN));
        table.setItems(dataErrors);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void readAll(ActionEvent actionEvent) {
        errorRepository.readAllType(SystemArg.ERROR_TYPE_CHOSEN);
        reloadTable();
    }

    public void readSelections(ActionEvent actionEvent) {
        errorRepository.readSelectionsType(new ArrayList<>(table.getSelectionModel().getSelectedItems()), SystemArg.ERROR_TYPE_CHOSEN);
        reloadTable();
    }
}
