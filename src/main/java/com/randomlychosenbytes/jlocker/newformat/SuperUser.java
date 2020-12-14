package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.decryptKeyWithString;

public class SuperUser extends User {

    @Expose
    protected String encryptedSuperUMasterKeyBase64;

    public SuperUser(
            String hash,
            String encryptedUserMasterKeyBase64,
            String encryptedSuperUMasterKeyBase64
    ) {
        this.passwordHash = hash;
        this.encryptedSuperUMasterKeyBase64 = encryptedSuperUMasterKeyBase64;
        this.encryptedUserMasterKeyBase64 = encryptedUserMasterKeyBase64;
    }

    public SecretKey getSuperUMasterKeyBase64(String password) {
        return decryptKeyWithString(encryptedSuperUMasterKeyBase64, password);
    }
}
