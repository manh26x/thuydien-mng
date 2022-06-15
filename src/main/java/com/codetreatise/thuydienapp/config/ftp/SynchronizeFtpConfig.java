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
            if(ftpClient != null) {
                File folder = new File(configArg.getLocalWorkingDirectory());
                try{
                    for(File file : Objects.requireNonNull(folder.listFiles())) {
                        try {
                            FileInputStream inputStream;
                            boolean completed = false;
                            while (!completed) {
                                ftpClient.enterLocalPassiveMode();
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                log.info(ftpClient.getReplyString() + " file: " + file.getName());
                                logText.append("\nSEND " + file.getName());
                                inputStream = new FileInputStream(file);
                                OutputStream os = ftpClient.storeFileStream(configArg.getRemoteWorkingDirectory() + "/" + file.getName());
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = inputStream.read(buffer)) != -1) {
                                    os.write(buffer, 0, len);
                                }

                                inputStream.close();

                                os.close();
                                completed = ftpClient.completePendingCommand();
                            }
                            log.info(file.getName() + " is uploaded successfully!");
                            logText.append(" is uploaded successfully!");

                            if ((configArg.getTransferDirectory() != null || !configArg.getTransferDirectory().equals(""))) {
                                boolean isTransfer = file.renameTo(new File(configArg.getTransferDirectory() + "/" + file.getName()));
                                if (isTransfer) {
                                    log.info(file.getName() + " is transfer!");
                                    logText.append(" and transfer");
                                } else {
                                    log.error(file.getName() + " transfer error!");
                                    logText.append(" and transfer error!");
                                }
                            }
                        }catch (Exception ex) {
                            log.error(ex.getMessage());
                            EventTrigger.getInstance().setChange();
                            EventTrigger.getInstance().notifyObservers(
                                    EventObject.builder()
                                            .type(Constants.CONST_ERROR)
                                            .dataError( DataError.builder()
                                                    .title("FTP ERROR")
                                                    .message(ex.getMessage())
                                                    .menuName(configArg.getMenuName())
                                                    .type(Constants.FTP_TYPE)
                                                    .createTime(LocalDateTime.now())
                                                    .build())
                                            .build()
                            );
                            isError = true;

                        }

                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();
                        while (logText.toString().split("\n").length > 10) {
                            logText.delete(0, logText.indexOf("\n"));
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    if(!isError) {
                        EventTrigger.getInstance().notifyObservers(
                                EventObject.builder()
                                        .type(Constants.CONST_SUCCESS)
                                        .dataError( DataError.builder()
                                                .title("FTP SUCCESS")
                                                .message(null)
                                                .menuName(configArg.getMenuName())
                                                .type(Constants.FTP_TYPE)
                                                .createTime(LocalDateTime.now())
                                                .build())
                                        .build()
                        );
                    }

                }
                configArg.autoNextTime();

            }
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
