package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.model.ParamBoCT;
import com.codetreatise.thuydienapp.model.ParamCucTNN;
import com.codetreatise.thuydienapp.repository.BoCTRepository;
import com.codetreatise.thuydienapp.repository.CucTNNRepository;
import com.codetreatise.thuydienapp.utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ImportExcelFile implements Initializable {
    public ChoiceBox<TypeChooseFile> choiceTypeImport;
    public TextField textFile;

    public ImportExcelFile() {

    }

    public void close(ActionEvent event) {
        StageManager.getInstance().closeDialog();
    }

    public void importFile(ActionEvent event) {
        try {
            readFile(textFile.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chooseFile(MouseEvent mouseEvent) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(StageManager.getInstance().getPrimaryStage());
        textFile.setText(file.getAbsolutePath());

    }

    private void readFile(String file) throws IOException {
        Workbook wb2007 = new XSSFWorkbook(file);
        SqliteJdbc.getInstance().clearAllParams();
        SqliteJdbc.getInstance().clearAllData();
        getParamBoCT(wb2007);
        getParamCucTNN(wb2007);
        EventTrigger.getInstance().setChange();
        EventTrigger.getInstance().notifyObservers(null);
        StageManager.getInstance().closeDialog();

    }

    private void getParamCucTNN(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(1);
        Row row = sheet.getRow(2);
        String username = row.getCell(1).getStringCellValue();
        String password = sheet.getRow(3).getCell(1).getStringCellValue();
        String maTinh = sheet.getRow(4).getCell(1).getStringCellValue();
        String kyHieuCongTrinh = sheet.getRow(5).getCell(1).getStringCellValue();
        Integer status =(int) sheet.getRow(6).getCell(1).getNumericCellValue();
        Integer timer = (int) sheet.getRow(7).getCell(1).getNumericCellValue();
        Double numberOfParams = sheet.getRow(8).getCell(1).getNumericCellValue();

        ArrayList<ParamCucTNN> dataList = new ArrayList<>();
        for(int i =0; i < numberOfParams; i++) {
            row = sheet.getRow(10 + i);
            int j = 0;
            ParamCucTNN data = new ParamCucTNN();
            for (Iterator<Cell> it = row.cellIterator(); it.hasNext(); j++) {
                Cell cel = it.next();
                switch (j) {
                    case 0:
                        data.setName(cel.getStringCellValue().trim());
                        break;
                    case 1:
                        data.setThongSoDo(cel.getStringCellValue().trim());
                        break;
                    case 2:
                        data.setKyHieuTram(cel.getStringCellValue().trim());
                        break;
                    case 3:
                        data.setDvt(cel.getStringCellValue().trim());
                        break;
                    case 4:
                        data.setAddress((int) cel.getNumericCellValue());
                        break;
                }
            }
            dataList.add(data);
            SqliteJdbc.getInstance().addModbusParam(ModbusParam.builder()
                    .name(data.getName())
                    .dvt(data.getDvt())
                    .address(data.getAddress())
                    .build());
        }
        SystemArg.API_LIST.add(ApiConfig.builder()
                        .apiCallReady(status == 1)
                        .url(Constants.CUC_TNN_URL)
                        .username(username)
                        .password(password)
                        .timeScheduleCallApi(timer)
                        .name(Constants.CUC_TNN_NAME)
                .build());
        CucTNNRepository.getInstance().saveInfo(maTinh, kyHieuCongTrinh);
        CucTNNRepository.getInstance().saveAllParams(dataList);
//        SystemArg.DATA_LIST.clear();
//        SystemArg.DATA_LIST.addAll(dataList);
        try {
            DataConfig.saveFavorites(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getParamBoCT(Workbook wb2007) {
        Sheet sheet = wb2007.getSheetAt(0);
        Row row = sheet.getRow(1);
        Cell numberOfApiCell = row.getCell(1);
        double numberOfApi = numberOfApiCell.getNumericCellValue();
        ArrayList<ApiConfig> listApi = new ArrayList<>();
        int maxRow = 0;
        for(int i=0; i< numberOfApi; i ++) {
            ApiConfig apiConfig = new ApiConfig();
            for(int j=0;j<6;j++) {
                row = sheet.getRow(2+ (i*6) + j);
                Cell name = row.getCell(0);
                Cell value = row.getCell(1);
                switch (name.getStringCellValue()) {
                    case "name":
                        apiConfig.setName(Constants.BO_CT_NAME);
                        break;
                    case "url":
                        apiConfig.setUrl(value.getStringCellValue().trim());
                        break;
                    case "username":
                        apiConfig.setUsername(value.getStringCellValue().trim());
                        break;
                    case "password":
                        apiConfig.setPassword(value.getStringCellValue().trim());
                        break;
                    case "status":
                        apiConfig.setApiCallReady(value.getNumericCellValue() == 1.0);
                        break;
                    case "time":
                        apiConfig.setTimeScheduleCallApi((int) value.getNumericCellValue());
                        break;
                }
                maxRow = Math.max(maxRow, 2+ (i*6) + j);
            }
            listApi.add(apiConfig);
        }


        row = sheet.getRow(maxRow +1);
        Cell numberOfKeyCell = row.getCell(1);
        double numberOfKey = numberOfKeyCell.getNumericCellValue();
        ArrayList<ParamBoCT> dataList = new ArrayList<>();
        for(int i =0; i < numberOfKey; i++) {
            row = sheet.getRow(maxRow + 3 + i);
            int j = 0;
            ParamBoCT data = new ParamBoCT();
            for (Iterator<Cell> it = row.cellIterator(); it.hasNext(); j++) {
                Cell cel = it.next();
                switch (j) {
                    case 0:
                        data.setKey(cel.getStringCellValue().trim());
                        break;
                    case 1:
                        data.setTenChiTieu(cel.getStringCellValue().trim());
                        data.setName(cel.getStringCellValue().trim());
                        break;
                    case 2:
                        data.setMaThamSo(cel.getStringCellValue().trim());
                        break;
                    case 3:
                        data.setDvt(cel.getStringCellValue());
                        break;
                    case 4:
                        data.setNguon(cel.getStringCellValue());
                        break;
                    case 5:
                        data.setAddress((int) cel.getNumericCellValue());
                        break;
                    case 6:
//                        for(String apiName : cel.getStringCellValue().split(";")) {
//                            ApiConfig api = listApi.stream().filter(apiConfig -> apiConfig.getName().equals(apiName)).findFirst().orElse(new ApiConfig());
//                            if(api.getKeySends() == null) {
//                                api.setKeySends( new ArrayList<>());
//                            }
//                            api.getKeySends().add(data);
//                        }
                        break;
                }
            }
            dataList.add(data);
            SqliteJdbc.getInstance().addModbusParam(ModbusParam.builder()
                    .name(data.getName())
                    .dvt(data.getDvt())
                    .address(data.getAddress())
                    .build());
        }

        SystemArg.API_LIST.clear();
        SystemArg.API_LIST.addAll(listApi);
        BoCTRepository.getInstance().saveAll(dataList);
//        SystemArg.DATA_LIST.clear();
//        SystemArg.DATA_LIST.addAll(dataList);
        try {
            DataConfig.saveFavorites(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<TypeChooseFile> typeChooseImport = new ArrayList<>();
        typeChooseImport.add(new TypeChooseFile("REPLACE", "Replace all your config and remove old"));
        typeChooseImport.add(new TypeChooseFile("NONE_REPLACE", "Update and add your config"));
        choiceTypeImport.getItems().addAll(typeChooseImport);
        choiceTypeImport.getSelectionModel().select(0);
    }

    static class TypeChooseFile {
        private String type;
        private String description;

        public TypeChooseFile(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public TypeChooseFile() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
