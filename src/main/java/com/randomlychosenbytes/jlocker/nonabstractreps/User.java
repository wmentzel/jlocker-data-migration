package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class User extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -6899339135756518502L;

    public String sHash;
    public boolean isSuperUser;

    private byte[] encUserMasterKey;
    private byte[] encSuperUMasterKey;

    // transient variables don't get serialized!
    transient public static SecretKey decUserMasterKey = null; // no static, no initialization, add transient
    transient public SecretKey decSuperUMasterKey;
    transient public String decUserPW;

    public boolean isPasswordCorrect(String pw) {
        if (new SecurityManager().getHash(pw.getBytes()).equals(sHash)) {
            decUserPW = pw;

            // decrypt master keys
            decUserMasterKey = decryptKeyWithString(encUserMasterKey);

            if (isSuperUser) {
                decSuperUMasterKey = decryptKeyWithString(encSuperUMasterKey);
            }

            return true;
        } else {
            return false;
        }
    }

    // Key is saved as string
    private SecretKey decryptKeyWithString(byte[] encryptedKey) {
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(decUserPW.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] bytes = dcipher.doFinal(encryptedKey);

            return new SecretKeySpec(bytes, "DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("* User.DecryptKeyWithString()... failed");
        }

        return null;
    }
}
