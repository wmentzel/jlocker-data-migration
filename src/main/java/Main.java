import com.randomlychosenbytes.jlocker.newformat.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.generateAndEncryptKey;
import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.getHash;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class Main extends javax.swing.JFrame {

    public static void main(String[] args) {

        String superUserPassword = "11111111";
        String limitedUserPassword = "22222222";

        File oldJLockerDatFile = new File(Util.getAppDir(), "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, limitedUserPassword);

        File newJLockerDatFile = new File(Util.getAppDir(), "src/test/data/new_jlocker.dat");

        Settings newSettings = new Settings();
        newSettings.lockerMinSizes = (List<Integer>) oldData.settings.get("LockerMinSizes");
        newSettings.lockerOverviewFontSize = (Integer) oldData.settings.get("LockerOverviewFontSize");
        newSettings.numOfBackups = (Integer) oldData.settings.get("NumOfBackups");

        SuperUser superUser = new SuperUser(
                superUserPassword,
                getHash(superUserPassword),
                generateAndEncryptKey(superUserPassword),
                generateAndEncryptKey(superUserPassword)
        );

        RestrictedUser restrictedUser = new RestrictedUser(
                limitedUserPassword,
                superUser.getUserMasterKey()
        );

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

                    List<ManagementUnit> managementUnits = new LinkedList<>();
                    for (abstractreps.ManagementUnit oldManagementUnit : oldWalk.mus) {

                        ManagementUnit newManagementUnit = new ManagementUnit();

                        switch (oldManagementUnit.mType) {
                            case abstractreps.ManagementUnit.LOCKERCOLUMN: {

                                LockerCabinet newCabinet = new LockerCabinet();

                                List<Locker> lockers = new LinkedList<>();
                                for (nonabstractreps.Locker oldLocker : oldManagementUnit.cabinet.lockers) {

                                    Locker newLocker = new Locker();

                                    newLocker.id = oldLocker.sID;
                                    newLocker.firstName = oldLocker.sName;
                                    newLocker.lastName = oldLocker.sSirName;
                                    newLocker.sizeInCm = oldLocker.iSize;
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
                                    //locker.encryptedCodes = oldLocker.encCodes;

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
                                room.schoolClassName = oldManagementUnit.room.sName;

                                newManagementUnit.room = room;
                                break;
                            }

                        }
                    }

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

        NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, limitedUserPassword, newBuildings, newSettings, newTasks, superUser, restrictedUser
        );
    }

}
