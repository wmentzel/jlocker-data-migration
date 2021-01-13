package nonabstractreps;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.randomlychosenbytes.jlocker.utils.CryptoKt.getHash;

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

    public Pair<SecretKey, SecretKey> getSecretKeys(String pw) {

        if (getHash(pw).equals(sHash)) {
            // decrypt master keys
            SecretKey decUserMasterKey = decryptKeyWithString(encUserMasterKey, pw);

            if (isSuperUser) {
                return new Pair<>(decryptKeyWithString(encSuperUMasterKey, pw), decUserMasterKey);
            } else {
                return new Pair<>(null, decUserMasterKey);
            }
        } else {
            return null;
        }
    }

    // Key is saved as string
    private SecretKey decryptKeyWithString(byte[] encryptedKey, String pw) {
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(pw.getBytes());
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
