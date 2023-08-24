package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.DataObj;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.model.APIDataModel;
import com.codetreatise.thuydienapp.model.ParamBoCT;
import com.codetreatise.thuydienapp.model.ParamCucTNN;
import com.codetreatise.thuydienapp.repository.BoCTRepository;
import com.codetreatise.thuydienapp.repository.CucTNNRepository;
import com.codetreatise.thuydienapp.repository.ResultRepository;
import com.codetreatise.thuydienapp.utils.Constants;
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
import java.util.*;
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
    ObservableList<DataObj> dataObservable = FXCollections.observableArrayList();



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
//        startDate.setValue(LocalDate.now());
//        dataChosen.getItems().addAll(Arrays.asList( new Integer[] {200, 201, 400, 404, 500}));
        setColProperties();
        loadDataList();
        super.initApiMenuGen();
    }

    public void reset(ActionEvent event) {
        apiConfig = SystemArg.API_LIST.stream().filter(e-> Objects.equals(e.getName(), SystemArg.NAME_API_CHOSEN)).findFirst().get();
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
        LocalDate toDateLocal = endDate.getValue();
        Date toDate =  Date.from(toDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<APIDataModel> resultList = new ArrayList<>();
        if (apiConfig.getName().equals(Constants.BO_CT_NAME)) {
            resultList = BoCTRepository.getInstance().findBoCTDataResultByDate(toDate);
        }
        if (apiConfig.getName().equals(Constants.CUC_TNN_NAME)) {
            resultList = CucTNNRepository.getInstance().findCucTNNDataResultByDate(toDate);
        }
        Map<String, DataObj> dataObjMap = new HashMap<>();
        resultList.forEach(e -> {
            DataObj dataObj = new DataObj();
            dataObj.setTimeSend(e.getTime());
            DataObj.ValueAndCode valueAndCode = new DataObj.ValueAndCode();
            valueAndCode.setValue((double) Math.round(e.getValue() * 100) / 100.0f);
            valueAndCode.setCodeResponse(e.getCode());
            if (dataObjMap.get(e.getTime().toString()) == null) {
                dataObj.getData().put(e.getMaThamSo(), valueAndCode);
                dataObjMap.put(e.getTime().toString(), dataObj);
            } else {
                dataObj = dataObjMap.get(e.getTime().toString());
                dataObj.getData().put(e.getMaThamSo(), valueAndCode);
            }

        });
        dataObservable.addAll(dataObjMap.values());
        dataTable.setItems(dataObservable);
    }

    private void setColProperties() {
        dataTable.getColumns().clear();
        TableColumn timeCol = new TableColumn();
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSendString"));
        timeCol.setText("Thời gian");
        dataTable.getColumns().add(timeCol);
        if (apiConfig.getName().equals(Constants.BO_CT_NAME)) {
            List<ParamBoCT> paramBoCTS = BoCTRepository.getInstance().findAll();
            paramBoCTS.forEach(key -> {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
                tableColumn.setText(key.getMaThamSo());
                tableColumn.setCellFactory(e -> new TableCell<ObservableList<String>, Map<String, DataObj.ValueAndCode>>() {
                    @Override
                    public void updateItem(Map<String, DataObj.ValueAndCode> item, boolean empty) {
                        // Always invoke super constructor.
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        }else if(item.get(key.getMaThamSo()) != null) {
                            setText(String.valueOf(item.get(key.getMaThamSo()).getValue()));
                            // If index is two we set the background color explicitly.
                            if (item.get(key.getMaThamSo()).getCodeResponse() >= 300) {
                                this.setStyle("-fx-background-color: #ff7c7c;");
                            } else {
                                this.setStyle("-fx-background-color: #3ade3a;");
                            }
                        } else {
                            setText(null);
                        }
                    }
                });
                dataTable.getColumns().add(tableColumn);
            });
        }
        if (apiConfig.getName().equals(Constants.CUC_TNN_NAME)) {
            List<ParamCucTNN> paramsCucTNN = CucTNNRepository.getInstance().findAll();
            paramsCucTNN.forEach(param -> {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
                tableColumn.setText(param.getName());
                tableColumn.setCellFactory(e -> new TableCell<ObservableList<String>, Map<String, DataObj.ValueAndCode>>() {
                    @Override
                    public void updateItem(Map<String, DataObj.ValueAndCode> item, boolean empty) {
                        // Always invoke super constructor.
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        }else if(item.get(param.getName()) != null) {
                            setText(String.valueOf(item.get(param.getName()).getValue()));
                            // If index is two we set the background color explicitly.
                            if (item.get(param.getName()).getCodeResponse() >= 300) {
                                this.setStyle("-fx-background-color: #ff7c7c;");
                            } else {
                                this.setStyle("-fx-background-color: #3ade3a;");
                            }
                        } else {
                            setText(null);
                        }
                    }
                });
                dataTable.getColumns().add(tableColumn);
            });
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

    public void config(ActionEvent actionEvent) {
        if(apiConfig.getName().equals(Constants.BO_CT_NAME)) {
            stageManager.createModal(FxmlView.CONFIG_BO_CT_PARAMS);
        }
        if(apiConfig.getName().equals(Constants.CUC_TNN_NAME)) {
            stageManager.createModal(FxmlView.CONFIG_CUC_TNN_PARAMS);
        }
    }
}
