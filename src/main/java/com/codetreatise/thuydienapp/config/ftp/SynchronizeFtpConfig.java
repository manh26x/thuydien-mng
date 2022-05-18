package com.codetreatise.thuydienapp.config.ftp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.util.TimerTask;

@Slf4j
public class SynchronizeFtpConfig extends TimerTask {

    private SynchronizeFtpConfig() {
    }
    private static SynchronizeFtpConfig instance;

    public static SynchronizeFtpConfig getInstance() {
        if(instance == null) {
            instance = new SynchronizeFtpConfig();
        }
        return instance;
    }

    public void autoSendFtp() throws IllegalBlockSizeException, IOException, BadPaddingException, ClassNotFoundException, InterruptedException {
        FtpArgSaved ftpArgSaved = getFtpConfigArg();
        ftpArgSaved.getFtpConfigArg().values().stream().parallel().filter(FtpConfigArg::checkReady).forEach(configArg -> {
            FTPClient ftpClient = getFtpClientConnected(configArg);

            if(ftpClient != null) {
                File folder = new File(configArg.getLocalWorkingDirectory());
                try{
                    for(File file : folder.listFiles()) {
                        FileInputStream inputStream;
                        boolean completed = false;
                        while (!completed) {
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
                        }

                        log.info(file.getName() + " is uploaded successfully!");
                        if((configArg.getTransferDirectory() != null || !configArg.getTransferDirectory().equals(""))  ){
                            boolean isTransfer = file.renameTo(new File(configArg.getTransferDirectory() + "/" + file.getName()));
                            if(isTransfer) {
                                log.info(file.getName() + " is transfer!");
                            } else {
                                log.error(file.getName() + " transfer error!");
                            }
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();

                    } catch (IOException e) {
                        e.printStackTrace();
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
