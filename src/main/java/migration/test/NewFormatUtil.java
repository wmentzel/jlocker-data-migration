package migration.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.ModuleDeserializer;
import com.randomlychosenbytes.jlocker.model.Building;
import com.randomlychosenbytes.jlocker.model.JsonRoot;
import com.randomlychosenbytes.jlocker.model.RestrictedUser;
import com.randomlychosenbytes.jlocker.model.SuperUser;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class NewFormatUtil {

    private static final String cryptoAlgorithmName = "DES";

    public static void saveData(
            File file,
            String superUserPassword,
            String restrictedUserPassword,
            List<com.randomlychosenbytes.jlocker.model.Building> buildings,
            com.randomlychosenbytes.jlocker.model.Settings settings,
            List<com.randomlychosenbytes.jlocker.model.Task> tasks,
            SuperUser superUser,
            RestrictedUser restrictedUser
    ) {

        try {

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            try (Writer writer = new FileWriter(file)) {

                gson.toJson(new JsonRoot(
                        encrypt(gson.toJson(buildings), decryptKeyWithString(superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)),
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

    // Key is saved as string
    private static SecretKey decryptKeyWithString(byte[] encryptedKey, String pw) {
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(pw.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] bytes = dcipher.doFinal(encryptedKey);

            return new SecretKeySpec(bytes, "DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException |
                 IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("* nonabstractreps.User.DecryptKeyWithString()... failed");
        }

        return null;
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
            return new String(dcipher.doFinal(base64StringToBytes(base64)), StandardCharsets.UTF_8);
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
            return str.getBytes(StandardCharsets.UTF_8);
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

    public static String bytesToBase64String(byte[] bytes) {
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

    public static NewData loadFromCustomFile(File file, String superUserPassword, String restrictedUserPassword) throws FileNotFoundException {

        System.out.print("* reading " + file.getName() + "... ");

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Reader reader = new FileReader(file);

        JsonRoot root = gson.fromJson(reader, JsonRoot.class);

        NewData newData = new NewData();

        newData.setSuperUser(root.getSuperUser());
        newData.setRestrictedUser(root.getRestrictedUser());

        newData.buildings = unsealAndDeserializeBuildings(
                root.getEncryptedBuildingsBase64(), decryptKeyWithString(newData.superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)
        );

        newData.tasks = root.getTasks();
        newData.settings = root.getSettings();

        return newData;

    }

    public static List<Building> unsealAndDeserializeBuildings(String encryptedBuildingsBase64, SecretKey key) {
        String json = decrypt(encryptedBuildingsBase64, key);
        Gson gson = new GsonBuilder().registerTypeAdapter(
                com.randomlychosenbytes.jlocker.model.Module.class,
                new ModuleDeserializer<com.randomlychosenbytes.jlocker.model.Module>()
        ).excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());
    }
}
