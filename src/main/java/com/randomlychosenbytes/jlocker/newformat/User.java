package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.decryptKeyWithString;

/**
 * Represents a User of the program. There are two different kinds a the moment
 * a restricted user and a super user. The super user can do everything the
 * restricted user can, plus he can view/edit the locker codes.
 */
public abstract class User {

    @Expose
    public String passwordHash;

    @Expose
    public String encryptedUserMasterKeyBase64;

    transient protected String decryptedUserPassword;

    public SecretKey getUserMasterKey() {
        return decryptKeyWithString(encryptedUserMasterKeyBase64, decryptedUserPassword);
    }
}
