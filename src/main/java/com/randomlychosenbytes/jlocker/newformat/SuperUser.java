package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.decryptKeyWithString;

public class SuperUser extends User {

    @Expose
    protected String encSuperUMasterKeyBase64;

    public SuperUser(
            String password,
            String hash,
            String encUserMasterKeyBase64,
            String encSuperUMasterKeyBase64) {

        this.decUserPW = password;
        this.sHash = hash;
        this.encSuperUMasterKeyBase64 = encSuperUMasterKeyBase64;
        this.encUserMasterKeyBase64 = encUserMasterKeyBase64;
    }

    public SecretKey getSuperUMasterKeyBase64() {
        return decryptKeyWithString(encSuperUMasterKeyBase64, decUserPW);
    }
}
