package com.codetreatise.thuydienapp.controller;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.ftp.FtpArgSaved;
import com.codetreatise.thuydienapp.config.ftp.FtpConfig;
import com.codetreatise.thuydienapp.config.ftp.FtpConfigArg;
import com.codetreatise.thuydienapp.view.FxmlView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    public TextField transferDirectory;


    private final StageManager stageManager;

    @FXML
    public ComboBox timeChosen;
    ObservableList<File> dataObservable = FXCollections.observableArrayList();;

    public FtpController() {
        stageManager = StageManager.getInstance();
    }


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] protocols = new String[] {"FTP", "FTPS"};
        ftpProtocol.getItems().addAll(Arrays.stream(protocols)
                .collect(Collectors.toList()));
        ftpProtocol.getSelectionModel().select(0);
        int[] timeChosenList = new int[] { 5, 10, 15, 30, 60 };
        timeChosen.getItems().addAll(Arrays.stream(timeChosenList)
                .boxed()
                .collect(Collectors.toList()));

        super.initApiMenuGen();
        setColProperties();
        reset(null);

    }

    void loadFile() {
        dataObservable.clear();
        if(localDirectory.getText() != null) {
            File folder = new File(localDirectory.getText());
            dataObservable.addAll(Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toList()));
            fileListTable.setItems(dataObservable);
        }
    }
    void setColProperties() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

    }
    public void reset(ActionEvent actionEvent) throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        FtpConfigArg ftpConfigArg = FtpConfig.getFtpConfigArg(SystemArg.NAME_FTP_CHOSEN);
        username.setText(ftpConfigArg.getAccount());
        password.setText(ftpConfigArg.getPassword());
        ipAddress.setText(ftpConfigArg.getIpAddress());
        ftpProtocol.getSelectionModel().select(ftpConfigArg.getProtocol() != null && ftpConfigArg.getProtocol().equals("ftp")  ? 0 : 1 );
        localDirectory.setText(ftpConfigArg.getLocalWorkingDirectory());
        remoteDirectory.setText(ftpConfigArg.getRemoteWorkingDirectory());
        rbReady.setSelected(ftpConfigArg.getReady() != null && ftpConfigArg.getReady());
        rbNotReady.setSelected(ftpConfigArg.getReady() == null || !ftpConfigArg.getReady());
        timeChosen.getSelectionModel().select(ftpConfigArg.getTimeSchedule());
        port.setText(String.valueOf(ftpConfigArg.getPort()));
        transferDirectory.setText(ftpConfigArg.getTransferDirectory());
        passive.setSelected(ftpConfigArg.getIsPassive() != null && ftpConfigArg.getIsPassive());
        loadFile();
    }

    public void save(ActionEvent actionEvent) throws IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        if(!valid()) {
            return;
        }
        if(rbReady.isSelected()) {
            if(!testConection(null)) {
                this.lbMessage.setText("Check connection!");
                return;
            }

        }
        FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
        ftpArgSaved.getFtpConfigArg().put(SystemArg.NAME_FTP_CHOSEN, FtpConfigArg.builder()
                .account(username.getText())
                .password(password.getText())
                .ipAddress(ipAddress.getText())
                .localWorkingDirectory(localDirectory.getText())
                .protocol(ftpProtocol.getSelectionModel().getSelectedItem().equals("FTP") ? "ftp" : "ftps")
                .isPassive(passive.isSelected())
                .remoteWorkingDirectory(remoteDirectory.getText())
                .port(Integer.parseInt(port.getText()))
                .ready(rbReady.isSelected())
                .timeSchedule( (Integer) timeChosen.getSelectionModel().getSelectedItem())
                .transferDirectory(transferDirectory.getText())
                .build());
        FtpConfig.saveFavorites(ftpArgSaved);

    }
    boolean valid() {
        boolean valid = true;
        String message = "";
        String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
        String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");
        port.setStyle(successStyle);
        ipAddress.setStyle(successStyle);
        localDirectory.setStyle(successStyle);
        remoteDirectory.setStyle(successStyle);
        if(ipAddress.getText() == null || ipAddress.getText().trim().equals("")) {
            message += "IP or hostname is required!\n";
            ipAddress.setStyle(errorStyle);
            valid = false;
        }
        if(port.getText() == null || port.getText().trim().equals("") || !port.getText().trim().matches("\\d+")) {
            message += "Port is required and it must be a digit 0-9\n";
            port.setStyle(errorStyle);
            valid = false;
        }
        if(localDirectory.getText() == null || localDirectory.getText().trim().equals("")) {
            message += "Local working directory is required!\n";
            ipAddress.setStyle(errorStyle);
            valid = false;
        }
        if(remoteDirectory.getText() == null || remoteDirectory.getText().trim().equals("")) {
            message += "Remote working directory is required!\n";
            ipAddress.setStyle(errorStyle);
            valid = false;
        }
        if(timeChosen.getSelectionModel() == null || timeChosen.getSelectionModel().isEmpty()) {
            message += "Time Sync send file must be chosen\n";
            timeChosen.setStyle(errorStyle);
            valid = false;
        }
        lbMessage.setText(message);
        return valid;
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

    public void chooseFileTransfer(MouseEvent mouseEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();

        configuringDirectoryChooser(directoryChooser);
        File dir = directoryChooser.showDialog(stageManager.getPrimaryStage());
        if (dir != null) {
            transferDirectory.setText(dir.getAbsolutePath());
        } else {
            transferDirectory.setText(null);
        }
    }

    public void deleteFtp(ActionEvent actionEvent) throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        FtpArgSaved ftpArgSaved = FtpConfig.getFtpConfig();
        ftpArgSaved.getFtpConfigArg().remove(SystemArg.NAME_FTP_CHOSEN);
        FtpConfig.saveFavorites(ftpArgSaved);
        stageManager.switchScene(FxmlView.TIMING_MODBUS);
    }
}
