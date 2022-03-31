package com.codetreatise.thuydienapp.config.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Configuration
@EnableScheduling
public class SynchronizeFtpConfig {

    @Scheduled( initialDelay = 10 * 1000, fixedDelay = 10 * 1000)
    public void autoSendFtp() throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException, InterruptedException {
        FtpConfigArg configArg = getFtpConfigArg();
        if(Boolean.FALSE.equals(configArg.checkReady())) {
            return;
        }
        FTPClient ftpClient = getFtpClientConnected(configArg);

        if(ftpClient != null) {
            File folder = new File(configArg.getLocalWorkingDirectory());
            try{
                for(File file : folder.listFiles()) {
                    FileInputStream inputStream;
                    boolean completed = false;
                    int i = 0;
                    while (!completed || i <=10) {
                        ftpClient.enterLocalPassiveMode();
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        log.info(ftpClient.getReplyString() + " file: " + file.getName());
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
                        i++;
                    }

                    if(completed) {
                        log.info(file.getName() + " is uploaded successfully!");
                        if((configArg.getTransferDirectory() != null || !configArg.getTransferDirectory().equals(""))  ){
                            boolean isTransfer = file.renameTo(new File(configArg.getTransferDirectory() + "/" + file.getName()));
                            if(isTransfer) {
                                log.info(file.getName() + " is transfer!");
                            } else {
                                log.error(file.getName() + " transfer error!");
                            }
                        }
                    } else {
                        log.error(file.getName() + " is uploaded failed!");
                    }

                }
            }finally {
                ftpClient.logout();
                ftpClient.disconnect();

            }
            }
        configArg.autoNextTime();
        FtpConfig.saveFavorites(configArg);

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
            return null;
        }
    }

    public FtpConfigArg getFtpConfigArg() throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException {
        return FtpConfig.getFtpConfig();
    }

}
