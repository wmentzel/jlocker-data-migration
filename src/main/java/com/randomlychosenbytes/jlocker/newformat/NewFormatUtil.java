package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class NewFormatUtil {

    private NewFormatUtil() {
    }

    private static final String cryptoAlgorithmName = "DES";

    public static void saveData(
            File file,
            String superUserPassword,
            String restrictedUserPassword,
            List<Building> buildings,
            Settings settings,
            List<Task> tasks,
            SuperUser superUser,
            RestrictedUser restrictedUser
    ) {

        try {

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            try (Writer writer = new FileWriter(file)) {

                gson.toJson(new JsonRoot(
                        encrypt(gson.toJson(buildings), superUser.getUserMasterKey()),
                        settings,
                        tasks,
                        superUser,
                        restrictedUser
                ), writer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String encrypt(String s, SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            return bytesToBase64String(ecipher.doFinal(getUtf8Bytes(s)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String decrypt(String base64, SecretKey key) {
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);
            dcipher.init(Cipher.DECRYPT_MODE, key);
            return new String(dcipher.doFinal(base64StringToBytes(base64)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String getHash(String pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] bytes = getUtf8Bytes(pw);
            m.update(bytes, 0, bytes.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SecretKey decryptKeyWithString(String encKeyBase64, String pw) { // Key is saved as string
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new SecretKeySpec(dcipher.doFinal(base64StringToBytes(encKeyBase64)), cryptoAlgorithmName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static String bytesToBase64String(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private static byte[] base64StringToBytes(String str) {
        return DatatypeConverter.parseBase64Binary(str);
    }

    public static String generateAndEncryptKey(String pw) {
        try {
            return encryptKeyWithString(KeyGenerator.getInstance(cryptoAlgorithmName).generateKey(), pw);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String encryptKeyWithString(SecretKey key, String pw) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return bytesToBase64String(ecipher.doFinal(key.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
