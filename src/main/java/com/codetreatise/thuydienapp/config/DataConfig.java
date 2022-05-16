package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class DataConfig {

    @lombok.Data
    static class DataSaveObject implements Serializable {
        public Integer TIME_SCHEDULE_SYNC_MODBUS;
        public Date NEXT_TIME_SCHEDULE_SYNC_MODBUS;
        public Integer TIME_SCHEDULE_CALL_API;
        public Date NEXT_TIME_SCHEDULE_CALL_API;

        public String API_CALL_URL;

        public String MODBUS_IP;
        public Integer MODBUS_PORT;
        public Byte UNIT;

        public boolean MODBUS_SYNC_READY;
        public boolean API_CALL_API_READY;

        public String API_USERNAME;
        public String API_PASSWORD;
        public List<Data> DATA_LIST;
        public String TOKEN;
        public List<ApiConfig> API_LIST;
        public Boolean LOGIN;

        private void createNew(Integer TIME_SCHEDULE_SYNC_MODBUS, Date NEXT_TIME_SCHEDULE_SYNC_MODBUS, Integer TIME_SCHEDULE_CALL_API, Date NEXT_TIME_SCHEDULE_CALL_API, String API_CALL_URL, String MODBUS_IP, Integer MODBUS_PORT, byte UNIT,boolean MODBUS_SYNC_READY, boolean API_CALL_API_READY, String API_USERNAME, String API_PASSWORD, List<Data> dataList, List<ApiConfig> apiConfigs, String token, Boolean LOGIN) {

            this.TIME_SCHEDULE_SYNC_MODBUS = TIME_SCHEDULE_SYNC_MODBUS;
            this.NEXT_TIME_SCHEDULE_SYNC_MODBUS = NEXT_TIME_SCHEDULE_SYNC_MODBUS;
            this.TIME_SCHEDULE_CALL_API = TIME_SCHEDULE_CALL_API;
            this.NEXT_TIME_SCHEDULE_CALL_API = NEXT_TIME_SCHEDULE_CALL_API;
            this.API_CALL_URL = API_CALL_URL;
            this.MODBUS_IP = MODBUS_IP;
            this.MODBUS_PORT = MODBUS_PORT;
            this.MODBUS_SYNC_READY = MODBUS_SYNC_READY;
            this.API_CALL_API_READY = API_CALL_API_READY;
            this.API_USERNAME = API_USERNAME;
            this.API_PASSWORD = API_PASSWORD;
            this.DATA_LIST = dataList;
            this.UNIT = UNIT;
            this.API_LIST = apiConfigs;
            this.TOKEN = token;
            this.LOGIN = LOGIN;
        }

        public DataSaveObject() {
            createNew(
                    SystemArg.TIME_SCHEDULE_SYNC_MODBUS,
                    SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS,
                    SystemArg.TIME_SCHEDULE_CALL_API,
                    SystemArg.NEXT_TIME_SCHEDULE_CALL_API,
                    SystemArg.API_CALL_URL,
                    SystemArg.MODBUS_IP,
                    SystemArg.MODBUS_PORT,
                    SystemArg.UNIT,
                    SystemArg.MODBUS_SYNC_READY,
                    SystemArg.API_CALL_API_READY,
                    SystemArg.API_USERNAME,
                    SystemArg.API_PASSWORD,
                    SystemArg.DATA_LIST,
                    SystemArg.API_LIST,
                    SystemArg.TOKEN,
                     SystemArg.LOGIN
            );
        }

        public void writeToSystemArg() {
            SystemArg.TIME_SCHEDULE_SYNC_MODBUS = TIME_SCHEDULE_SYNC_MODBUS;
            SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS = NEXT_TIME_SCHEDULE_SYNC_MODBUS;
            SystemArg.TIME_SCHEDULE_CALL_API = TIME_SCHEDULE_CALL_API;
            SystemArg.NEXT_TIME_SCHEDULE_CALL_API = NEXT_TIME_SCHEDULE_CALL_API;
            SystemArg.API_CALL_URL = API_CALL_URL;
            SystemArg.MODBUS_IP = MODBUS_IP;
            SystemArg.MODBUS_PORT = MODBUS_PORT;
            SystemArg.MODBUS_SYNC_READY = MODBUS_SYNC_READY;
            SystemArg.API_CALL_API_READY = API_CALL_API_READY;
            SystemArg.API_USERNAME = API_USERNAME;
            SystemArg.API_PASSWORD = API_PASSWORD;
            SystemArg.DATA_LIST = DATA_LIST;
            SystemArg.UNIT = UNIT;
            if(API_LIST == null) {
                API_LIST = new ArrayList<>();
            }
            SystemArg.API_LIST = API_LIST;
            SystemArg.LOGIN = LOGIN;
            SystemArg.TOKEN = TOKEN;
        }

    }

    public static final File DATA_HOME;
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File HOST_FILE ;
    public static final String AES_RULES = "AES";
    public static DataSaveObject dataSavedObject;

    static {
        DATA_HOME = new File(USER_HOME, ".thuydienmng" + File.separator + "data");
        HOST_FILE =  new File(DATA_HOME,
                "host.conf.ser");
    }


    private static void checkDataHome() {
        if (!DATA_HOME.exists() || !DATA_HOME.isDirectory()) {
            DATA_HOME.mkdirs();
        }
    }
    private static void checkHostFile() throws IOException {
        checkDataHome();
        if (!HOST_FILE.exists() || !HOST_FILE.isFile()) {
            HOST_FILE.createNewFile();
            saveFavorites(null);
        }
    }


    public static DataSaveObject getHostsList() throws IOException,
            ClassNotFoundException, IllegalBlockSizeException,
            BadPaddingException {
        DataSaveObject dataSaveObject = null;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            checkHostFile();
            fin = new FileInputStream(HOST_FILE);
            in  = new ObjectInputStream(fin);
            if (fin.available() > 0) {

                Object obj = in.readObject();
                if (obj instanceof DataSaveObject) {
                    dataSaveObject = (DataSaveObject) obj;
                } else {
                    SealedObject so = (SealedObject) obj;
                    dataSaveObject = (DataSaveObject) so
                            .getObject(getCipher(Cipher.DECRYPT_MODE));

                }
            }
            dataSaveObject.writeToSystemArg();
            return dataSaveObject;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static Cipher getCipher(int mode) {
        final byte[] encodedKey = { 28, -9, -23, 35, -93, -47, -28, 55, -83,
                -82, 101, -79, 36, 59, 77, -121 };
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_RULES);
            SecretKeySpec spec = new SecretKeySpec(encodedKey, AES_RULES);
            cipher.init(mode, spec);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exp) {
            exp.printStackTrace();
        }
        return null;
    }


    public static void saveFavorites(DataSaveObject dataSaveObject) throws IOException {
        if(dataSaveObject == null) {
            dataSaveObject = new DataSaveObject();
        }
        checkDataHome();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(HOST_FILE));
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            if (cipher == null) {
                // We do not have AES provider. so just save the favorites
                // unencrypted.

                out.writeObject(dataSaveObject);
            } else {
                SealedObject so = new SealedObject((Serializable) dataSaveObject,
                        cipher);
                out.writeObject(so);
                dataSavedObject = dataSaveObject;
            }
        } catch (IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            out = null;
        }
    }

}
