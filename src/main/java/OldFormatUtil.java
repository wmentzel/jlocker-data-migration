import nonabstractreps.Building;
import nonabstractreps.Pair;
import nonabstractreps.Task;
import nonabstractreps.User;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 */
public class OldFormatUtil {

    private OldFormatUtil() {
    }

    public static OldData loadData(File jlockerDatFile, String superUserPassword, String limitedUserPassword) {

        List<User> users;
        SealedObject sealedBuildingsObject;
        List<Task> tasks;
        TreeMap settings;

        try (
                FileInputStream fis = new FileInputStream(jlockerDatFile);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            users = (List<User>) ois.readObject();
            sealedBuildingsObject = (SealedObject) ois.readObject();
            tasks = (LinkedList<Task>) ois.readObject();
            settings = (TreeMap) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        User superUser = users.get(0);
        User limitedUser = users.get(1);

        Pair<SecretKey, SecretKey> superUserKeys = superUser.getSecretKeys(superUserPassword);

        if (superUserKeys.getX() == null || superUserKeys.getY() == null) {
            throw new RuntimeException("Super user password does not match");
        }

        Pair<SecretKey, SecretKey> userKeys = limitedUser.getSecretKeys(limitedUserPassword);

        if (userKeys.getY() == null) {
            throw new RuntimeException("Limited user password does not match");
        }

        List<Building> buildings = unsealAndDeserializeBuildings(
                sealedBuildingsObject,
                superUserKeys.getY()
        );

        if (buildings == null) {
            throw new RuntimeException("Could not decrypt buildings with user password");
        }

        OldData oldData = new OldData();
        oldData.buildings = buildings;
        oldData.users = users;
        oldData.tasks = tasks;
        oldData.settings = settings;
        return oldData;
    }

    public static String getHash(byte[] pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(pw, 0, pw.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("nonabstractreps.SecurityManager.getHash  failed!");
        }

        return "";
    }

    /**
     * Turns a series of bytes to an object.
     * Found at:
     * http://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet
     */
    public static <T> T deserialize(byte[] data) throws Exception {

        try (
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in)
        ) {
            Object o = is.readObject();
            return (T) o;
        }
    }

    private static byte[] decryptObject(SealedObject so, SecretKey key) throws Exception {
        Cipher dcipher = Cipher.getInstance("DES");
        dcipher.init(Cipher.DECRYPT_MODE, key);
        return (byte[]) so.getObject(dcipher);
    }

    /**
     * Unseals the buildings object. This can't be done in the
     * loadFromCustomFile method, because the data is loaded before the password
     * was entered.
     */
    public static List<Building> unsealAndDeserializeBuildings(SealedObject sealedBuildingsObject, SecretKey key) {
        try {
            byte b[] = decryptObject(sealedBuildingsObject, key);
            return deserialize(b);
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList();
        }
    }
}
