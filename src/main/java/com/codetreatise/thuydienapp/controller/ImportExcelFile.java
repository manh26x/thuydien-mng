package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.event.EventTrigger;
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
                        apiConfig.setName(value.getStringCellValue().trim());
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
        ArrayList<Data> dataList = new ArrayList<>();
        for(int i =0; i < numberOfKey; i++) {
            row = sheet.getRow(maxRow + 3 + i);
            int j = 0;
            Data data = new Data();
            for (Iterator<Cell> it = row.cellIterator(); it.hasNext(); j++) {
                Cell cel = it.next();
                switch (j) {
                    case 0:
                        data.setKey(cel.getStringCellValue().trim());
                        break;
                    case 1:
                        data.setTenChiTieu(cel.getStringCellValue().trim());
                        break;
                    case 2:
                        data.setMaThongSo(cel.getStringCellValue().trim());
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
                        for(String apiName : cel.getStringCellValue().split(";")) {
                            ApiConfig api = listApi.stream().filter(apiConfig -> apiConfig.getName().equals(apiName)).findFirst().orElse(new ApiConfig());
                            if(api.getKeySends() == null) {
                                api.setKeySends( new ArrayList<>());
                            }
                            api.getKeySends().add(data);
                        }
                        break;
                }
                data.setQuantity(2);

            }
            dataList.add(data);
        }

        SystemArg.API_LIST.clear();
        SystemArg.API_LIST.addAll(listApi);
        SystemArg.DATA_LIST.clear();
        SystemArg.DATA_LIST.addAll(dataList);
        try {
            DataConfig.saveFavorites(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        EventTrigger.getInstance().setChange();
        EventTrigger.getInstance().notifyObservers(null);
        StageManager.getInstance().closeDialog();

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
