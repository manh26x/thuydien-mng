package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Result;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.repository.ResultRepository;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ApiConfigController extends BaseController implements Initializable {
    @FXML
    public TextField apiAddress;
    @FXML
    public TextField usernameApi;
    @FXML
    public TextField passwordApi;
    @FXML
    public RadioButton rbReady;
    @FXML
    public RadioButton rbNotReady;
    @FXML
    public TextField timeSyncApi;
    @FXML
    public DatePicker startDate;
    @FXML
    public DatePicker endDate;
    @FXML
    public ComboBox<Integer> dataChosen;
    public Label lbMessage;

    @FXML
    public TextArea result;
    public TableView dataTable;
    public TableColumn colTime;
    public TableColumn colRequest;
    public TableColumn colResponse;
    public TableColumn colTrangThai;
    public ComboBox timeSyncChosen;
    private ApiConfig apiConfig;


    private final ResultRepository resultRepository;
    ObservableList<Result> dataObservable = FXCollections.observableArrayList();;

    public ApiConfigController() {
        resultRepository = ResultRepository.getInstance();
    }

    @Override
    protected void reload() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int[] timeChosenList = new int[] { 5, 10, 15, 30, 60 };
        timeSyncChosen.getItems().addAll(Arrays.stream(timeChosenList)
                .boxed()
                .collect(Collectors.toList()));
        reset(null);
        endDate.setValue(LocalDate.now());
        startDate.setValue(LocalDate.now());
        dataChosen.getItems().addAll(Arrays.asList( new Integer[] {200, 201, 400, 404, 500}));
        setColProperties();
        loadDataList();
        super.initApiMenuGen();
    }

    public void reset(ActionEvent event) {
        apiConfig = SystemArg.API_LIST.stream().filter(e-> e.getName() == SystemArg.NAME_API_CHOSEN).findFirst().get();
        apiAddress.setText(apiConfig.getUrl());
        usernameApi.setText(apiConfig.getUsername());
        passwordApi.setText(apiConfig.getPassword());
        rbReady.setSelected(apiConfig.isApiCallReady());
        rbNotReady.setSelected(!apiConfig.isApiCallReady());
        switch (apiConfig.getTimeScheduleCallApi()) {
            case 5:
                timeSyncChosen.getSelectionModel().select(0);
                break;
            case 10:
                timeSyncChosen.getSelectionModel().select(1);
                break;
            case 15:
                timeSyncChosen.getSelectionModel().select(2);
                break;
            case 30:
                timeSyncChosen.getSelectionModel().select(3);
                break;
            case 60:
                timeSyncChosen.getSelectionModel().select(4);
                break;
        }
    }



    @FXML
    public void save(ActionEvent event) {
        if(isValid()) {

            apiConfig.setPassword(passwordApi.getText());
            apiConfig.setUsername(usernameApi.getText() != null ? usernameApi.getText().trim() : null);
            apiConfig.setUrl(apiAddress.getText().trim());
            apiConfig.setTimeScheduleCallApi((Integer) timeSyncChosen.getSelectionModel().getSelectedItem());
            apiConfig.setApiCallReady(rbReady.isSelected());
            reset(null);
            try {
                DataConfig.saveFavorites(null);
            } catch (Exception e) {}
        }
    }

    private void loadDataList() {
        dataObservable.clear();
        LocalDate fromDateLocal = startDate.getValue();
        LocalDate toDateLocal = endDate.getValue();
        Date fromDate = Date.from(fromDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate =  Date.from(toDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        toDate.setMinutes(59);
        toDate.setSeconds(59);
        toDate.setHours(23);
        dataObservable.addAll(resultRepository.findAllByApiAndTimeSendAfterAndTimeSendBefore(apiConfig.getName(), apiConfig.getUrl(),fromDate, toDate));
        dataTable.setItems(dataObservable);
    }

    private void setColProperties() {
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("codeResponse"));
        colRequest.setCellValueFactory(new PropertyValueFactory<>("request"));
        colResponse.setCellValueFactory(new PropertyValueFactory<>("response"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeSendString"));
    }
    public void resfresh(ActionEvent event) {
        initialize(null, null);
    }
    private boolean isValid() {
        boolean valid = true;
        String message = "";
        String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
        apiAddress.setStyle(successStyle);
        usernameApi.setStyle(successStyle);
        passwordApi.setStyle(successStyle);
        timeSyncChosen.setStyle(successStyle);
        if(rbReady.isSelected()) {
            if(apiAddress.getText().trim().equals("")) {
                message += "Api Address required\n";
                lbMessage.setText(message);
                apiAddress.setStyle(errorStyle);
                valid = false;
            }
            if(usernameApi.getText().trim().equals("")) {
                message += "Username required\n";
                lbMessage.setText(message);
                usernameApi.setStyle(errorStyle);
                valid = false;
            }
            if(passwordApi.getText().equals("")) {
                message += "Password required\n";
                lbMessage.setText(message);
                passwordApi.setStyle(errorStyle);
                valid = false;
            }
            if(timeSyncChosen.getSelectionModel().isEmpty()) {
                message += "Thoi gian must be chosen\n";
                timeSyncChosen.setStyle(errorStyle);
                valid = false;
            }
            if(apiConfig.getKeySends() == null || apiConfig.getKeySends().isEmpty()) {
                message += "Please add your key in Edit tabs\n";
                valid = false;
            }
        }
        lbMessage.setText(message);


        return valid;
    }

    public void search(ActionEvent event) {
        loadDataList();
    }

    public void saveAndCall(ActionEvent event) {
        apiConfig.setNextTimeScheduleCallApi(new Date());
        save(null);

    }

    public void viewData(ActionEvent event) {
    }

    public void deleteApi(ActionEvent actionEvent) {
        SystemArg.API_LIST.remove(apiConfig);
        try {
            DataConfig.saveFavorites(null);
            super.timeSyncModbus(null);
        } catch (Exception e) {}
    }


    public void addField(ActionEvent event) {
        super.stageManager.createModal(FxmlView.API_FIELD_CONFIG);
    }
}
