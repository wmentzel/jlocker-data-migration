import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.model.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.randomlychosenbytes.jlocker.utils.CryptoKt.base64StringToBytes;
import static com.randomlychosenbytes.jlocker.utils.CryptoKt.bytesToBase64String;


/*
 * Yes, there are two NewFormatUtils...
 * Classes from the default package are used in the functions of this class,
 * so it has itself to reside in the default package.
 */
public class NewFormatUtil {

    public static NewData loadFromCustomFile(File file, String superUserPassword, String restrictedUserPassword) {

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try (Reader reader = new FileReader(file)) {

            JsonRoot root = gson.fromJson(reader, JsonRoot.class);

            NewData newData = new NewData();

            newData.superUser = root.getSuperUser();
            newData.restrictedUser = root.getRestrictedUser();

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

    public static List<Building> unsealAndDeserializeBuildings(String encryptedBuildingsBase64, SecretKey key) throws Exception {
        String json = decrypt(encryptedBuildingsBase64, key);
        Gson gson = new GsonBuilder().registerTypeAdapter(
                com.randomlychosenbytes.jlocker.model.Module.class,
                new ModuleDeserializer<com.randomlychosenbytes.jlocker.model.Module>()
        ).excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());
    }

    public static NewData convert(OldData oldData, String superUserPassword, String restrictedUserPassword, SecretKey oldSuperUserKey) {

        SuperUser superUser = new SuperUser(superUserPassword);

        SecretKey superUserMasterKey = decryptKeyWithString(superUser.getEncryptedSuperUMasterKeyBase64(), superUserPassword);

        RestrictedUser restrictedUser = new RestrictedUser(
                restrictedUserPassword,
                decryptKeyWithString(superUser.getEncryptedUserMasterKeyBase64(), superUserPassword)
        );

        Settings newSettings = new Settings();
        newSettings.setLockerMinSizes((List<Integer>) oldData.settings.get("LockerMinSizes"));
        newSettings.setLockerOverviewFontSize((Integer) oldData.settings.get("LockerOverviewFontSize"));
        newSettings.setNumOfBackups((Integer) oldData.settings.get("NumOfBackups"));

        List<Task> newTasks = new LinkedList<>();

        for (nonabstractreps.Task oldTask : oldData.tasks) {

            Task task = new Task(oldTask.sDescription);
            task.setCreationDate(oldTask.sDate);
            task.setDone(oldTask.isDone);

            newTasks.add(task);
        }

        List<Building> newBuildings = new LinkedList<>();
        for (nonabstractreps.Building oldBuilding : oldData.buildings) {
            Building newBuilding = new Building(oldBuilding.sName);

            List<Floor> floors = new LinkedList<>();
            for (nonabstractreps.Floor oldFloor : oldBuilding.floors) {

                Floor newFloor = new Floor(oldFloor.sName);

                List<Walk> newWalks = new LinkedList<>();
                for (nonabstractreps.Walk oldWalk : oldFloor.walks) {

                    Walk newWalk = new Walk(oldWalk.sName);

                    List<ModuleWrapper> newManagmentUnits = new LinkedList<>();
                    for (abstractreps.ManagementUnit oldManagementUnit : oldWalk.mus) {

                        com.randomlychosenbytes.jlocker.model.Module module = null;

                        switch (oldManagementUnit.mType) {
                            case abstractreps.ManagementUnit.LOCKERCOLUMN: {

                                LockerCabinet newCabinet = new LockerCabinet();

                                List<Locker> lockers = new LinkedList<>();
                                for (nonabstractreps.Locker oldLocker : oldManagementUnit.cabinet.lockers) {

                                    Locker newLocker = new Locker();

                                    newLocker.setId(oldLocker.sID);

                                    if (oldLocker.sName.isEmpty() && oldLocker.sSirName.isEmpty()) {
                                        newLocker.empty();
                                    } else {
                                        Pupil pupil = new Pupil();
                                        pupil.setFirstName(oldLocker.sName);
                                        pupil.setLastName(oldLocker.sSirName);
                                        pupil.setHeightInCm(oldLocker.iSize);
                                        pupil.setSchoolClassName(oldLocker.sClass);
                                        pupil.setRentedFromDate(oldLocker.sFrom);
                                        pupil.setRentedUntilDate(oldLocker.sUntil);
                                        pupil.setPaidAmount(oldLocker.iMoney);
                                        pupil.setPreviouslyPaidAmount(oldLocker.iPrevAmount);
                                        pupil.setHasContract(oldLocker.hasContract);
                                        newLocker.moveInNewOwner(pupil);
                                    }

                                    newLocker.setOutOfOrder(oldLocker.isOutOfOrder);
                                    newLocker.setLockCode(oldLocker.sLock);
                                    newLocker.setNote(oldLocker.sNote);
                                    newLocker.setCurrentCodeIndex(oldLocker.iCurrentCodeIndex);

                                    if (oldLocker.encCodes != null) {
                                        String[] newCodes = new String[oldLocker.encCodes.length];

                                        for (int i = 0; i < oldLocker.encCodes.length; i++) {
                                            newCodes[i] = decrypt(bytesToBase64String(oldLocker.encCodes[i]), oldSuperUserKey);
                                        }

                                        newLocker.setCodes(newCodes, superUserMasterKey);
                                    }

                                    lockers.add(newLocker);
                                }


                                newCabinet.setLockers(lockers);

                                module = newCabinet;
                                break;
                            }
                            case abstractreps.ManagementUnit.STAIRCASE: {

                                Staircase staircase = new Staircase(oldManagementUnit.staircase.sName);
                                module = staircase;
                                break;
                            }
                            case abstractreps.ManagementUnit.ROOM: {

                                Room room = new Room(oldManagementUnit.room.sName, oldManagementUnit.room.sClass);

                                module = room;
                                break;
                            }
                        }

                        newManagmentUnits.add(new ModuleWrapper(module));
                    }

                    Collections.reverse(newManagmentUnits);
                    newWalk.setModuleWrappers(newManagmentUnits);
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

    private static final String cryptoAlgorithmName = "DES";

    public static void saveData(
            File file,
            String superUserPassword,
            String restrictedUserPassword,
            List<Building> buildings,
            Settings settings,
            List<Task> tasks,
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
            return new String(dcipher.doFinal(base64StringToBytes(base64)), "UTF-8");
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
            return str.getBytes("UTF-8");
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
}
