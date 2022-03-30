package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.config.ftp.FtpConfigArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class FtpController  extends BaseController implements Initializable {
    @FXML
    public ComboBox<String> ftpProtocol;

    public TextField localDirectory;
    public TextField ipAddress;
    public TextField port;
    public TextArea logText;
    public TextField username;
    public TextField password;
    public TextField remoteDirectory;

    public CheckBox passive;
    public TableView<File> fileListTable;
    public TableColumn colName;
    public Label lbMessage;
    public RadioButton rbReady;
    public ToggleGroup readyGroup;
    public RadioButton rbNotReady;


    @Autowired
    @Lazy
    private StageManager stageManager;

    @FXML
    public ComboBox timeChosen;
    ObservableList<File> dataObservable = FXCollections.observableArrayList();;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] protocols = new String[] {"FTP", "FTPS"};
        ftpProtocol.getItems().addAll(Arrays.stream(protocols)
                .collect(Collectors.toList()));

        int[] timeChosenList = new int[] { 5, 10, 15, 30, 60 };
        timeChosen.getItems().addAll(Arrays.stream(timeChosenList)
                .boxed()
                .collect(Collectors.toList()));

        super.initApiMenuGen();
        setColProperties();

    }

    void loadFile() {
        dataObservable.clear();
        File folder = new File(localDirectory.getText());
        dataObservable.addAll(Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toList()));
        fileListTable.setItems(dataObservable);
    }
    void setColProperties() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

    }
    public void reset(ActionEvent actionEvent) throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        FtpConfigArg ftpConfigArg = FtpConfig.getFtpConfig();
        username.setText(ftpConfigArg.getAccount());
        password.setText(ftpConfigArg.getPassword());
        ipAddress.setText(ftpConfigArg.getIpAddress());
        ftpProtocol.getSelectionModel().select(ftpConfigArg.getProtocol().equals("ftp")  ? 0 : 1);
        localDirectory.setText(ftpConfigArg.getLocalWorkingDirectory());
        remoteDirectory.setText(ftpConfigArg.getRemoteWorkingDirectory());
        rbReady.setSelected(ftpConfigArg.getReady());
        rbNotReady.setSelected(!ftpConfigArg.getReady());
        timeChosen.getSelectionModel().select(ftpConfigArg.getTimeSchedule());
        port.setText(String.valueOf(ftpConfigArg.getPort()));
    }

    public void save(ActionEvent actionEvent) throws IOException {
        if(rbReady.isSelected()) {

        } else {
            FtpConfig.saveFavorites(FtpConfigArg.builder()
                    .account(username.getText().trim())
                    .password(password.getText())
                    .ipAddress(ipAddress.getText())
                    .localWorkingDirectory(localDirectory.getText())
                    .protocol(ftpProtocol.getSelectionModel().getSelectedItem().equals("FTP") ? "ftp" : "ftps")
                    .isPassive(passive.isSelected())
                    .remoteWorkingDirectory(remoteDirectory.getText())
                    .port(Integer.parseInt(port.getText()))
                            .ready(rbReady.isSelected())
                            .timeSchedule( (Integer) timeChosen.getSelectionModel().getSelectedItem())
                    .build());
        }
    }

    public void chooseFile(MouseEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();

        configuringDirectoryChooser(directoryChooser);
        File dir = directoryChooser.showDialog(stageManager.getPrimaryStage());
        if (dir != null) {
            localDirectory.setText(dir.getAbsolutePath());
            loadFile();

        } else {
            localDirectory.setText(null);
        }
    }
    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {

        // Set tiêu đề cho DirectoryChooser
        directoryChooser.setTitle("Select Some Directories");


        // Sét thư mục bắt đầu nhìn thấy khi mở DirectoryChooser
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public boolean testConection(ActionEvent actionEvent) throws IOException {
        try {
            FTPClient ftpClient = new FTPClient();
            if(ftpProtocol.getSelectionModel().isSelected(1)) {
                ftpClient = new FTPSClient( true );
            }
            ftpClient.connect(ipAddress.getText().trim(),Integer.parseInt(port.getText().trim()));

            ftpClient.login(username.getText().trim(), password.getText());
            if(passive.isSelected()) {
                ftpClient.enterRemotePassiveMode();
                ftpClient.enterLocalActiveMode();
            }

            ftpClient.changeWorkingDirectory(remoteDirectory.getText().trim());

            String msg = ftpClient.getReplyString();

            this.logText.setText(msg) ;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
