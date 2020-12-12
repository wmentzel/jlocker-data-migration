package com.randomlychosenbytes.jlocker.newformat;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.encryptKeyWithString;
import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.getHash;

public class RestrictedUser extends User {

    public RestrictedUser(String password, SecretKey ukey) {
        passwordHash = getHash(password); // MD5 hash
        encryptedUserMasterKeyBase64 = encryptKeyWithString(ukey, password);
    }
}
