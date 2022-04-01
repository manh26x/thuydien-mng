package com.codetreatise.thuydienapp.config.ftp;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FtpConfig {
    public static final File DATA_HOME;
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File HOST_FILE ;
    public static final String AES_RULES = "AES";
    static {
        DATA_HOME = new File(USER_HOME, ".thuydienmng" + File.separator + "ftp.config");
        HOST_FILE =  new File(DATA_HOME,
                "ftp.conf.ser");
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


    public static FtpArgSaved getFtpConfig() throws IOException,
            ClassNotFoundException, IllegalBlockSizeException,
            BadPaddingException {
        FtpArgSaved dataSaveObject = null;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            checkHostFile();
            fin = new FileInputStream(HOST_FILE);
            in  = new ObjectInputStream(fin);
            if (fin.available() > 0) {

                Object obj = in.readObject();
                if (obj instanceof FtpConfigArg) {
                    dataSaveObject = (FtpArgSaved) obj;
                } else {
                    SealedObject so = (SealedObject) obj;
                    dataSaveObject = (FtpArgSaved) so
                            .getObject(getCipher(Cipher.DECRYPT_MODE));

                }
            }
            if(dataSaveObject == null) {
                dataSaveObject = new FtpArgSaved();
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


    public static void saveFavorites(FtpArgSaved dataSaveObject) throws IOException {
        if(dataSaveObject == null) {
            dataSaveObject = new FtpArgSaved();
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

    public static FtpConfigArg getFtpConfigArg(String ftpName)throws IOException,
            ClassNotFoundException, IllegalBlockSizeException,
            BadPaddingException  {
        FtpArgSaved dataSaveObject = null;
        FileInputStream fin = null;
        ObjectInputStream in = null;
        try {
            checkHostFile();
            fin = new FileInputStream(HOST_FILE);
            in  = new ObjectInputStream(fin);
            if (fin.available() > 0) {

                Object obj = in.readObject();
                if (obj instanceof FtpConfigArg) {
                    dataSaveObject = (FtpArgSaved) obj;
                } else {
                    SealedObject so = (SealedObject) obj;
                    dataSaveObject = (FtpArgSaved) so
                            .getObject(getCipher(Cipher.DECRYPT_MODE));

                }
            }
            if(dataSaveObject == null) {
                dataSaveObject = new FtpArgSaved();
            }
            return dataSaveObject.getFtpConfigArg().get(ftpName);
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
