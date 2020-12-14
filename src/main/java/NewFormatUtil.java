import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.newformat.*;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.*;

/*
 * Yes, there are two NewFormatUtils...
 * Classes from the default package are used in the functions of this class,
 * so it has itself to reside in the default package.
 */
public class NewFormatUtil {

    public static NewData loadFromCustomFile(File file, String superUserPassword, String restrictedUserPassword) {

        System.out.print("* reading " + file.getName() + "... ");

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try (Reader reader = new FileReader(file)) {

            JsonRoot root = gson.fromJson(reader, JsonRoot.class);

            NewData newData = new NewData();

            newData.superUser = root.superUser;
            newData.restrictedUser = root.restrictedUser;

            newData.buildings = unsealAndDeserializeBuildings(
                    root.encryptedBuildingsBase64, newData.superUser.getUserMasterKey(superUserPassword)
            );

            newData.tasks = root.tasks;
            newData.settings = root.settings;

            return newData;

        } catch (Exception ex) {
        }

        return null;
    }

    public static List<Building> unsealAndDeserializeBuildings(String encryptedBuildingsBase64, SecretKey key) throws Exception {
        String json = decrypt(encryptedBuildingsBase64, key);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());
    }

    public static NewData convert(OldData oldData, String superUserPassword, String restrictedUserPassword, SecretKey oldSuperUserKey) {

        SuperUser superUser = new SuperUser(
                OldFormatUtil.getHash(superUserPassword.getBytes()),
                generateAndEncryptKey(superUserPassword),
                generateAndEncryptKey(superUserPassword)
        );

        RestrictedUser restrictedUser = new RestrictedUser(
                restrictedUserPassword,
                superUser.getUserMasterKey(superUserPassword)
        );

        Settings newSettings = new Settings();
        newSettings.lockerMinSizes = (List<Integer>) oldData.settings.get("LockerMinSizes");
        newSettings.lockerOverviewFontSize = (Integer) oldData.settings.get("LockerOverviewFontSize");
        newSettings.numOfBackups = (Integer) oldData.settings.get("NumOfBackups");

        List<Task> newTasks = new LinkedList<>();

        for (nonabstractreps.Task oldTask : oldData.tasks) {

            Task task = new Task();

            task.description = oldTask.sDescription;
            task.creationDate = oldTask.sDate;
            task.isDone = oldTask.isDone;

            newTasks.add(task);
        }

        List<Building> newBuildings = new LinkedList<>();
        for (nonabstractreps.Building oldBuilding : oldData.buildings) {
            Building newBuilding = new Building();

            List<Floor> floors = new LinkedList<>();
            for (nonabstractreps.Floor oldFloor : oldBuilding.floors) {

                Floor newFloor = new Floor();

                List<Walk> newWalks = new LinkedList<>();
                for (nonabstractreps.Walk oldWalk : oldFloor.walks) {

                    Walk newWalk = new Walk();

                    List<ManagementUnit> newManagmentUnits = new LinkedList<>();
                    for (abstractreps.ManagementUnit oldManagementUnit : oldWalk.mus) {

                        ManagementUnit newManagementUnit = new ManagementUnit();

                        newManagementUnit.type = oldManagementUnit.mType;

                        switch (oldManagementUnit.mType) {
                            case abstractreps.ManagementUnit.LOCKERCOLUMN: {

                                LockerCabinet newCabinet = new LockerCabinet();

                                List<Locker> lockers = new LinkedList<>();
                                for (nonabstractreps.Locker oldLocker : oldManagementUnit.cabinet.lockers) {

                                    Locker newLocker = new Locker();

                                    newLocker.id = oldLocker.sID;
                                    newLocker.firstName = oldLocker.sName;
                                    newLocker.lastName = oldLocker.sSirName;
                                    newLocker.heightInCm = oldLocker.iSize;
                                    newLocker.schoolClassName = oldLocker.sClass;
                                    newLocker.rentedFromDate = oldLocker.sFrom;
                                    newLocker.rentedUntilDate = oldLocker.sUntil;
                                    newLocker.hasContract = oldLocker.hasContract;
                                    newLocker.paidAmount = oldLocker.iMoney;
                                    newLocker.previoulyPaidAmount = oldLocker.iPrevAmount;
                                    newLocker.isOutOfOrder = oldLocker.isOutOfOrder;
                                    newLocker.lockCode = oldLocker.sLock;
                                    newLocker.note = oldLocker.sNote;
                                    newLocker.currentCodeIndex = oldLocker.iCurrentCodeIndex;

                                    if (oldLocker.encCodes != null) {
                                        String[] newCodes = new String[oldLocker.encCodes.length];

                                        for (int i = 0; i < oldLocker.encCodes.length; i++) {
                                            newCodes[i] = encrypt(decrypt(bytesToBase64String(oldLocker.encCodes[i]), oldSuperUserKey), superUser.getSuperUMasterKeyBase64(superUserPassword));
                                        }
                                        newLocker.encryptedCodes = newCodes;
                                    }

                                    lockers.add(newLocker);
                                }


                                newCabinet.lockers = lockers;

                                newManagementUnit.lockerCabinet = newCabinet;
                                break;
                            }
                            case abstractreps.ManagementUnit.STAIRCASE: {

                                Staircase staircase = new Staircase();

                                staircase.name = oldManagementUnit.staircase.sName;

                                newManagementUnit.staircase = staircase;
                                break;
                            }
                            case abstractreps.ManagementUnit.ROOM: {

                                Room room = new Room();

                                room.name = oldManagementUnit.room.sName;
                                room.schoolClassName = oldManagementUnit.room.sClass;

                                newManagementUnit.room = room;
                                break;
                            }
                        }
                        newManagmentUnits.add(newManagementUnit);
                    }
                    newWalk.name = oldWalk.sName;
                    newWalk.managementUnits = newManagmentUnits;
                    newWalks.add(newWalk);
                }

                newFloor.name = oldFloor.sName;
                newFloor.walks = newWalks;

                floors.add(newFloor);
            }

            newBuilding.floors = floors;
            newBuilding.name = oldBuilding.sName;

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
