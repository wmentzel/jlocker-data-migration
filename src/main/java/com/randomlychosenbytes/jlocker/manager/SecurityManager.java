package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.nonabstractreps.Building;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * The SecurityManager class handles everything regarding encryption and
 * decryption.
 */
public class SecurityManager {
    /**
     * Retuns a MD5 hash to a given array of bytes.
     */
    public String getHash(byte[] pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(pw, 0, pw.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SecurityManager.getHash  failed!");
        }

        return "";
    }

    /**
     * Turns an object to a series of bytes.
     */
    public static byte[] serialize(Object obj) throws IOException {

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(out);
        ) {
            os.writeObject(obj);
            return out.toByteArray();
        }
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

    public static SealedObject encryptObject(byte o[], SecretKey key) throws Exception {
        Cipher ecipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        return new SealedObject(o, ecipher);
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
            System.out.print("* decrypting...");
            byte b[] = decryptObject(sealedBuildingsObject, key);
            System.out.println("successful!");
            return deserialize(b);
        } catch (Exception e) {
            System.out.println("failed");
            e.printStackTrace();
            return new LinkedList();
        }
    }
}
