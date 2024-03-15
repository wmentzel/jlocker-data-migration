package main;

import abstractreps.ManagementUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.ModuleDeserializer;
import com.randomlychosenbytes.jlocker.model.*;
import com.randomlychosenbytes.jlocker.model.Module;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NewFormatUtil {

    private static final String cryptoAlgorithmName = "DES";

    public static void saveData(
            File file,
            String superUserPassword,
            String restrictedUserPassword,
            List<com.randomlychosenbytes.jlocker.model.Building> buildings,
            com.randomlychosenbytes.jlocker.model.Settings settings,
            List<com.randomlychosenbytes.jlocker.model.Task> tasks,
            SuperUser superUser,
            RestrictedUser restrictedUser
    ) {

        try {

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            try (Writer writer = new FileWriter(file)) {

                gson.toJson(new JsonRoot(
                        encrypt(gson.toJson(buildings), decryptKeyWithString(superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)),
                        settings,
                        tasks,
                        superUser,
                        restrictedUser
                ), writer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Pair<SecretKey, SecretKey> getSecretKeys(String pw, nonabstractreps.User user) {

        if (getHash(pw).equals(user.sHash)) {
            // decrypt master keys
            SecretKey decUserMasterKey = decryptKeyWithString(user.encUserMasterKey, pw);

            if (user.isSuperUser) {
                return new Pair<>(decryptKeyWithString(user.encSuperUMasterKey, pw), decUserMasterKey);
            } else {
                return new Pair<>(null, decUserMasterKey);
            }
        } else {
            return null;
        }
    }

    // Key is saved as string
    private static SecretKey decryptKeyWithString(byte[] encryptedKey, String pw) {
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(pw.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] bytes = dcipher.doFinal(encryptedKey);

            return new SecretKeySpec(bytes, "DES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException |
                 IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("* nonabstractreps.User.DecryptKeyWithString()... failed");
        }

        return null;
    }

    public static String encrypt(String s, SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            return bytesToBase64String(ecipher.doFinal(getUtf8Bytes(s)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String decrypt(String base64, SecretKey key) {
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);
            dcipher.init(Cipher.DECRYPT_MODE, key);
            return new String(dcipher.doFinal(base64StringToBytes(base64)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String getHash(String pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] bytes = getUtf8Bytes(pw);
            m.update(bytes, 0, bytes.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SecretKey decryptKeyWithString(String encKeyBase64, String pw) { // Key is saved as string
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new SecretKeySpec(dcipher.doFinal(base64StringToBytes(encKeyBase64)), cryptoAlgorithmName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String bytesToBase64String(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private static byte[] base64StringToBytes(String str) {
        return DatatypeConverter.parseBase64Binary(str);
    }

    public static String generateAndEncryptKey(String pw) {
        try {
            return encryptKeyWithString(KeyGenerator.getInstance(cryptoAlgorithmName).generateKey(), pw);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String encryptKeyWithString(SecretKey key, String pw) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return bytesToBase64String(ecipher.doFinal(key.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static NewData loadFromCustomFile(File file, String superUserPassword, String restrictedUserPassword) {

        System.out.print("* reading " + file.getName() + "... ");

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try (Reader reader = new FileReader(file)) {

            JsonRoot root = gson.fromJson(reader, JsonRoot.class);

            NewData newData = new NewData();

            newData.setSuperUser(root.getSuperUser());
            newData.setRestrictedUser(root.getRestrictedUser());

            newData.buildings = unsealAndDeserializeBuildings(
                    root.getEncryptedBuildingsBase64(), decryptKeyWithString(newData.superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)
            );

            newData.tasks = root.getTasks();
            newData.settings = root.getSettings();

            return newData;

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return null;
    }

    public static List<com.randomlychosenbytes.jlocker.model.Building> unsealAndDeserializeBuildings(String encryptedBuildingsBase64, SecretKey key) throws Exception {
        String json = decrypt(encryptedBuildingsBase64, key);
        Gson gson = new GsonBuilder().registerTypeAdapter(
                com.randomlychosenbytes.jlocker.model.Module.class,
                new ModuleDeserializer<com.randomlychosenbytes.jlocker.model.Module>()
        ).excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<nonabstractreps.Building>>() {
        }.getType());
    }

    public static NewData convert(OldData oldData, String superUserPassword, String restrictedUserPassword, SecretKey oldSuperUserKey) {

        SuperUser superUser = new SuperUser(superUserPassword);

        RestrictedUser restrictedUser = new RestrictedUser(
                restrictedUserPassword,
                decryptKeyWithString(superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)
        );

        Settings newSettings = new Settings();
        newSettings.setLockerMinSizes((List<Integer>) oldData.settings.get("LockerMinSizes"));
        newSettings.setLockerOverviewFontSize((Integer) oldData.settings.get("LockerOverviewFontSize"));
        newSettings.setNumOfBackups((Integer) oldData.settings.get("NumOfBackups"));

        List<com.randomlychosenbytes.jlocker.model.Task> newTasks = new LinkedList<>();

        for (nonabstractreps.Task oldTask : oldData.tasks) {

            com.randomlychosenbytes.jlocker.model.Task task = new com.randomlychosenbytes.jlocker.model.Task(oldTask.sDescription);

            task.setCreationDate(oldTask.sDate);
            task.setDone(oldTask.isDone);

            newTasks.add(task);
        }

        List<com.randomlychosenbytes.jlocker.model.Building> newBuildings = new LinkedList<>();
        for (nonabstractreps.Building oldBuilding : oldData.buildings) {
            com.randomlychosenbytes.jlocker.model.Building newBuilding = new com.randomlychosenbytes.jlocker.model.Building(oldBuilding.sName);

            List<com.randomlychosenbytes.jlocker.model.Floor> floors = new LinkedList<>();
            for (nonabstractreps.Floor oldFloor : oldBuilding.floors) {

                com.randomlychosenbytes.jlocker.model.Floor newFloor = new com.randomlychosenbytes.jlocker.model.Floor(oldFloor.sName);

                List<com.randomlychosenbytes.jlocker.model.Walk> newWalks = new LinkedList<>();
                for (nonabstractreps.Walk oldWalk : oldFloor.walks) {

                    com.randomlychosenbytes.jlocker.model.Walk newWalk = new com.randomlychosenbytes.jlocker.model.Walk(oldWalk.sName);

                    List<com.randomlychosenbytes.jlocker.model.Module> newManagmentUnits = new LinkedList<>();
                    for (ManagementUnit oldManagementUnit : oldWalk.mus) {

                        switch (oldManagementUnit.mType) {
                            case ManagementUnit.LOCKERCOLUMN: {

                                com.randomlychosenbytes.jlocker.model.LockerCabinet newCabinet = new com.randomlychosenbytes.jlocker.model.LockerCabinet();

                                List<com.randomlychosenbytes.jlocker.model.Locker> lockers = new LinkedList<>();
                                for (nonabstractreps.Locker oldLocker : oldManagementUnit.cabinet.lockers) {

                                    com.randomlychosenbytes.jlocker.model.Locker newLocker = new com.randomlychosenbytes.jlocker.model.Locker();

                                    newLocker.setId(oldLocker.sID);

                                    if (!oldLocker.sName.isEmpty() || !oldLocker.sSirName.isEmpty()) {
                                        Pupil pupil = new Pupil();
                                        pupil.setFirstName(oldLocker.sName);
                                        pupil.setLastName(oldLocker.sSirName);
                                        pupil.setHeightInCm(oldLocker.iSize);
                                        pupil.setSchoolClassName(oldLocker.sClass);
                                        pupil.setRentedFromDate(oldLocker.sFrom);
                                        pupil.setRentedUntilDate(oldLocker.sUntil);
                                        pupil.setPaidAmount(oldLocker.iMoney);
                                        pupil.setPreviouslyPaidAmount(oldLocker.iPrevAmount);
                                        newLocker.moveInNewOwner(pupil);
                                    }

                                    newLocker.setOutOfOrder(oldLocker.isOutOfOrder);
                                    newLocker.setLockCode(oldLocker.sLock);
                                    newLocker.setNote(oldLocker.sNote);
                                    newLocker.setCurrentCodeIndex(oldLocker.iCurrentCodeIndex);

                                    if (oldLocker.encCodes != null) {
                                        String[] newCodes = new String[oldLocker.encCodes.length];

                                        for (int i = 0; i < oldLocker.encCodes.length; i++) {
                                            newCodes[i] = encrypt(decrypt(bytesToBase64String(oldLocker.encCodes[i]), oldSuperUserKey), decryptKeyWithString(superUser.getEncryptedSuperUMasterKeyBase64(), superUserPassword));
                                        }
                                        newLocker.setCodes(newCodes, decryptKeyWithString(superUser.getEncryptedSuperUMasterKeyBase64(), superUserPassword));
                                    }

                                    lockers.add(newLocker);
                                }


                                newCabinet.setLockers(lockers);
                                newManagmentUnits.add(newCabinet);
                                break;
                            }
                            case ManagementUnit.STAIRCASE: {
                                com.randomlychosenbytes.jlocker.model.Staircase staircase = new com.randomlychosenbytes.jlocker.model.Staircase(oldManagementUnit.staircase.sName);
                                newManagmentUnits.add(staircase);
                                break;
                            }
                            case ManagementUnit.ROOM: {
                                com.randomlychosenbytes.jlocker.model.Room room = new com.randomlychosenbytes.jlocker.model.Room(oldManagementUnit.room.sName, oldManagementUnit.room.sClass);
                                newManagmentUnits.add(room);
                                break;
                            }
                        }
                    }

                    Collections.reverse(newManagmentUnits);
                    newWalk.setModules(newManagmentUnits);
                    newWalks.add(newWalk);
                }

                newFloor.setWalks(newWalks);

                floors.add(newFloor);
            }
            newBuilding.setFloors(floors);
            newBuildings.add(newBuilding);
        }

        NewData newData = new NewData();

        newData.buildings = newBuildings;
        newData.restrictedUser = restrictedUser;
        newData.superUser = superUser;
        newData.settings = newSettings;
        newData.tasks = newTasks;

        return newData;
    }
}
