package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@SuppressWarnings("unused")
public class User extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -6899339135756518502L;

    private String sHash;
    private boolean isSuperUser;

    private byte[] encUserMasterKey;
    private byte[] encSuperUMasterKey;

    // transient variables don't get serialized!
    transient private static SecretKey decUserMasterKey = null; // no static, no initialization, add transient
    transient private SecretKey decSuperUMasterKey;
    transient private String decUserPW;

    public boolean isPasswordCorrect(String pw) {
        if (new SecurityManager().getHash(pw.getBytes()).equals(sHash)) {
            decUserPW = pw;

            // decrypt master keys
            decUserMasterKey = DecryptKeyWithString(encUserMasterKey);

            if (isSuperUser)
                decSuperUMasterKey = DecryptKeyWithString(encSuperUMasterKey);

            return true;
        } else
            return false;
    }

    private byte[] EncryptKeyWithString(SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(decUserPW.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] bytes = ecipher.doFinal(key.getEncoded());

            return bytes;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("* User.EncryptKeyWithString()... failed");
        }

        return null;
    }

    private SecretKey DecryptKeyWithString(byte[] enc_key) // Key is saved as string
    {
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(decUserPW.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] bytes = dcipher.doFinal(enc_key);

            return new SecretKeySpec(bytes, "DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("* User.DecryptKeyWithString()... failed");
        }

        return null;
    }

    public String getUserPW() {
        return decUserPW;
    }

    public byte[] getEncUserMasterKey() {
        return encUserMasterKey;
    }

    public byte[] getEncSuperUMasterKey() {
        return encSuperUMasterKey;
    }

    public SecretKey getUserMasterKey() {
        return decUserMasterKey;
    }

    public SecretKey getSuperUMasterKey() {
        return decSuperUMasterKey;
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public String getSHash() {
        return sHash;
    }

    public void setCurrentUserPW(String pw) {
        decUserPW = pw;
    }

    public void setSuperUser(boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    public void setSHash(String sHash) {
        this.sHash = sHash;
    }

    public void setEncSuperUMasterKey(byte[] encSuperUMasterKey) {
        this.encSuperUMasterKey = encSuperUMasterKey;
    }

    public void setEncUserMasterKey(byte[] encUserMasterKey) {
        this.encUserMasterKey = encUserMasterKey;
    }
}
