package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.decryptKeyWithString;

public class SuperUser extends User {

    @Expose
    protected String encSuperUMasterKeyBase64;

    public SuperUser(
            String hash,
            String encUserMasterKeyBase64,
            String encSuperUMasterKeyBase64
    ) {
        this.passwordHash = hash;
        this.encSuperUMasterKeyBase64 = encSuperUMasterKeyBase64;
        this.encryptedUserMasterKeyBase64 = encUserMasterKeyBase64;
    }

    public SecretKey getSuperUMasterKeyBase64(String password) {
        return decryptKeyWithString(encSuperUMasterKeyBase64, password);
    }
}
