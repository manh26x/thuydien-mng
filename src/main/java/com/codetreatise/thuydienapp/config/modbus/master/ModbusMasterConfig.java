package com.codetreatise.thuydienapp.config.modbus.master;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ModbusMasterConfig {
    public static final File DATA_HOME;
    public static final File USER_HOME = new File("./");
    public static final File HOST_FILE ;
    public static final String AES_RULES = "AES";
    static {
        DATA_HOME = new File(USER_HOME, ".thuydienmng" + File.separator + "modbus.config");
        HOST_FILE =  new File(DATA_HOME,
                "modbus.conf.ser");
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


    public static ModbusMasterArg getModbusConfig() throws IOException,
            ClassNotFoundException, IllegalBlockSizeException,
            BadPaddingException {
        ModbusMasterArg dataSaveObject = null;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            checkHostFile();
            fin = new FileInputStream(HOST_FILE);
            in  = new ObjectInputStream(fin);
            if (fin.available() > 0) {

                Object obj = in.readObject();
                if (obj instanceof ModbusMasterArg) {
                    dataSaveObject = (ModbusMasterArg) obj;
                } else {
                    SealedObject so = (SealedObject) obj;
                    dataSaveObject = (ModbusMasterArg) so
                            .getObject(getCipher(Cipher.DECRYPT_MODE));

                }
            }
            if(dataSaveObject == null) {
                dataSaveObject = new ModbusMasterArg();
            }
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


    public static void saveFavorites(ModbusMasterArg dataSaveObject) throws IOException {
        if(dataSaveObject == null) {
            dataSaveObject = new ModbusMasterArg();
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

    public static ModbusMasterArg getFtpConfigArg(String ftpName)throws IOException,
            ClassNotFoundException, IllegalBlockSizeException,
            BadPaddingException  {
        ModbusMasterArg dataSaveObject = null;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            checkHostFile();
            fin = new FileInputStream(HOST_FILE);
            in  = new ObjectInputStream(fin);
            if (fin.available() > 0) {

                Object obj = in.readObject();
                if (obj instanceof ModbusMasterArg) {
                    dataSaveObject = (ModbusMasterArg) obj;
                } else {
                    SealedObject so = (SealedObject) obj;
                    dataSaveObject = (ModbusMasterArg) so
                            .getObject(getCipher(Cipher.DECRYPT_MODE));

                }
            }
            if(dataSaveObject == null) {
                dataSaveObject = new ModbusMasterArg();
            }
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
}
