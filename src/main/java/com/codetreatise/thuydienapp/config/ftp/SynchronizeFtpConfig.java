package com.codetreatise.thuydienapp.config.ftp;

import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.utils.Constants;
import com.codetreatise.thuydienapp.utils.EventObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TimerTask;

@Slf4j
public class SynchronizeFtpConfig extends TimerTask {

    private SynchronizeFtpConfig() {
        logText = new StringBuilder();
    }
    private static SynchronizeFtpConfig instance;

    private StringBuilder logText;

    public static SynchronizeFtpConfig getInstance() {
        if(instance == null) {
            instance = new SynchronizeFtpConfig();
        }
        return instance;
    }

    public StringBuilder getLogText() {
        return logText;
    }

    public void setLogText(StringBuilder logText) {
        this.logText = logText;
    }

    public static void setInstance(SynchronizeFtpConfig instance) {
        SynchronizeFtpConfig.instance = instance;
    }


    public void autoSendFtp() throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException, InterruptedException {
        FtpArgSaved ftpArgSaved = getFtpConfigArg();
        ftpArgSaved.getFtpConfigArg().values().stream().parallel().filter(FtpConfigArg::checkReady).forEach(configArg -> {
            FTPClient ftpClient = getFtpClientConnected(configArg);
            boolean isError = false;
            StringBuilder content = new StringBuilder();
            boolean completed = false;
            if(ftpClient != null) {
                File folder = new File(configArg.getLocalWorkingDirectory());
                try{
                    if(folder.exists() && Objects.requireNonNull(folder.listFiles()).length > 0) {
                        File file = Objects.requireNonNull(folder.listFiles())[0];
                        if(file != null) {
                            try {
                                FileInputStream inputStream = null;
                                while (!completed) {
                                    ftpClient.enterLocalPassiveMode();
                                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                    log.info(ftpClient.getReplyString() + " file: " + file.getName());
                                    content.append("\nSEND ").append(file.getName());
                                    inputStream = new FileInputStream(file);
                                    completed = ftpClient.storeFile(configArg.getRemoteWorkingDirectory() + "/" + file.getName(), inputStream);
                                }
                                try {
                                    inputStream.close();
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                }

                                log.info(file.getName() + " is uploaded successfully!");
                                content.append(" is uploaded successfully!");

                                if ((configArg.getTransferDirectory() != null || !configArg.getTransferDirectory().equals(""))) {
                                    Files.move(file.toPath(), new File(configArg.getTransferDirectory() + "/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    log.info(file.getName() + " is transfer!");
                                    content.append(" and transfer");
                                }
                            }catch (Exception ex) {
                                log.error(ex.getMessage());
                                content.append(ex.getMessage());
                                isError = true;

                            }

                        }
                    } else {
                        completed = true;
                    }

                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    if(!completed) {
                        content.append(" upload failed!");
                        isError = true;
                    }
                    log.info(content.toString());
                    logText.append(content);
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();
                        while (logText.toString().split("\n").length > 10) {
                            logText.delete(0, logText.indexOf("\n")+1);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }


                }
                String type = isError ? Constants.CONST_ERROR :  Constants.CONST_SUCCESS;
                EventTrigger.getInstance().setChange();
                EventTrigger.getInstance().notifyObservers(
                        EventObject.builder()
                                .type(type)
                                .dataError( DataError.builder()
                                        .title("FTP Warning")
                                        .message(content.toString())
                                        .menuName(configArg.getMenuName())
                                        .type(Constants.FTP_TYPE)
                                        .createTime(LocalDateTime.now())
                                        .build())
                                .build()
                );

            }
            configArg.autoNextTime();

        });
        FtpConfig.saveFavorites(ftpArgSaved);



    }

    public FTPClient getFtpClientConnected(FtpConfigArg configArg) {
        try {
            FTPClient ftpClient = new FTPClient();
            if(configArg.getProtocol().equals("ftps")) {
                ftpClient = new FTPSClient( true );
            }
            ftpClient.connect(configArg.getIpAddress(),configArg.getPort());

            ftpClient.login(configArg.getAccount(), configArg.getPassword());
            if(Boolean.TRUE.equals(configArg.getIsPassive())) {
                ftpClient.pasv();
            }

            return ftpClient;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            logText.append("\nCannot connect to ").append(configArg.getIpAddress()).append(" : ").append(configArg.getPort());
            EventTrigger.getInstance().setChange();
            EventTrigger.getInstance().notifyObservers(
                    EventObject.builder()
                            .type(Constants.CONST_ERROR)
                            .dataError( DataError.builder()
                                    .title("FTP ERROR")
                                    .message(e.getMessage())
                                    .menuName(configArg.getMenuName())
                                    .type(Constants.FTP_TYPE)
                                    .createTime(LocalDateTime.now())
                                    .build())
                            .build()
                   );
            return null;
        }
    }

    public FtpArgSaved getFtpConfigArg() throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        return FtpConfig.getFtpConfig();
    }

    @SneakyThrows
    @Override
    public void run() {
        autoSendFtp();
    }
}
