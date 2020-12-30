import com.randomlychosenbytes.jlocker.newformat.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.List;

import static com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.decrypt;
import static junit.framework.TestCase.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class DataMigrationTest {

    public static List<Building> buildings;
    public static List<Task> tasks;
    public static Settings settings;

    private static SecretKey superUserKey;

    @BeforeClass
    public static void setup() {

        String superUserPassword = "11111111";
        String restrictedUserPassword = "22222222";

        File appDir = Util.getAppDir(false);

        File jlockerDatFile = new File(appDir, "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(jlockerDatFile, "11111111", "22222222");

        NewData newData = NewFormatUtil.convert(oldData, superUserPassword, restrictedUserPassword, oldData.users.get(0).getSecretKeys(superUserPassword).first);

        File newJLockerDatFile = new File(appDir, "src/test/data/jlocker.json");

        com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, restrictedUserPassword,
                newData.buildings,
                newData.settings,
                newData.tasks,
                newData.superUser,
                newData.restrictedUser
        );

        NewData newDataLoadedFromFile = NewFormatUtil.loadFromCustomFile(newJLockerDatFile, superUserPassword, restrictedUserPassword);

        superUserKey = newDataLoadedFromFile.superUser.getSuperUMasterKeyBase64(superUserPassword);

        buildings = newDataLoadedFromFile.buildings;
        tasks = newDataLoadedFromFile.tasks;
        settings = newDataLoadedFromFile.settings;
    }

    @Test
    public void numberOfBuildingsShouldBeCorrect() {
        assertEquals(2, buildings.size());
    }

    @Test
    public void numberOfFloorsShouldBeCorrect() {
        assertEquals(2, buildings.get(0).floors.size());
        assertEquals(1, buildings.get(1).floors.size());
    }

    @Test
    public void numberOfWalksShouldBeCorrect() {
        assertEquals(2, buildings.get(0).floors.get(0).walks.size());
        assertEquals(1, buildings.get(0).floors.get(1).walks.size());
        assertEquals(1, buildings.get(1).floors.get(0).walks.size());
    }

    @Test
    public void numberOfManagementUnitsShouldBeCorrect() {
        assertEquals(5, buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.size());
    }

    @Test
    public void numberOfLockersShouldBeCorrect() {
        List<ModuleWrapper> moduleWrappers = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers;
        assertEquals(3, moduleWrappers.get(2).getLockerCabinet().lockers.size());
        assertEquals(3, moduleWrappers.get(1).getLockerCabinet().lockers.size());
        assertEquals(3, moduleWrappers.get(0).getLockerCabinet().lockers.size());
    }

    @Test
    public void buildingNamesShouldMatch() {
        assertEquals("main building", buildings.get(0).name);
        assertEquals("second building", buildings.get(1).name);
    }

    @Test
    public void floorNamesShouldMatch() {
        assertEquals("ground floor", buildings.get(0).floors.get(0).name);
        assertEquals("1st floor", buildings.get(0).floors.get(1).name);
        assertEquals("ground floor", buildings.get(1).floors.get(0).name);
    }

    @Test
    public void walkNamesShouldMatch() {
        assertEquals("main walk", buildings.get(0).floors.get(0).walks.get(0).name);
        assertEquals("second walk", buildings.get(0).floors.get(0).walks.get(1).name);
        assertEquals("-", buildings.get(0).floors.get(1).walks.get(0).name);
        assertEquals("main walk", buildings.get(1).floors.get(0).walks.get(0).name);
    }

    @Test
    public void lockerIdsShouldMatch() {
        LockerCabinet cabinet = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(0).getLockerCabinet();
        assertEquals("1", cabinet.lockers.get(0).id);
        assertEquals("2", cabinet.lockers.get(1).id);
        assertEquals("3", cabinet.lockers.get(2).id);
    }

    @Test
    public void roomDataShouldMatch() {
        Room room = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(3).getRoom();

        assertEquals(room.name, "Some Classroom");
        assertEquals(room.schoolClassName, "12");
    }

    @Test
    public void stairCaseDataShouldMatch() {
        Staircase staircase = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(4).getStaircase();
        assertEquals(staircase.name, "Main Staircase");
    }

    @Test
    public void lockerTypesShouldMatch() {
        List<ModuleWrapper> moduleWrappers = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers;

        assertEquals(moduleWrappers.get(0).module.getClass().getSimpleName(), LockerCabinet.class.getSimpleName());
        assertEquals(moduleWrappers.get(1).module.getClass().getSimpleName(), LockerCabinet.class.getSimpleName());
        assertEquals(moduleWrappers.get(2).module.getClass().getSimpleName(), LockerCabinet.class.getSimpleName());

        assertEquals(moduleWrappers.get(3).module.getClass().getSimpleName(), Room.class.getSimpleName());
        assertEquals(moduleWrappers.get(4).module.getClass().getSimpleName(), Staircase.class.getSimpleName());
    }

    @Test
    public void shouldHaveCorrectDataForLocker1() {
        Locker locker = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(0).getLockerCabinet().lockers.get(0);
        assertEquals("Peggy", locker.pupil.lastName);
        assertEquals("Olsen", locker.pupil.firstName);
        assertEquals("11", locker.pupil.schoolClassName);
        assertEquals(200, locker.pupil.heightInCm);
        assertEquals("This is some note!", locker.note);
        assertEquals("01.01.2020", locker.pupil.rentedFromDate);
        assertEquals("01.01.2021", locker.pupil.rentedUntilDate);
        assertEquals("12-34-56", locker.lockCode);
        assertEquals(150, locker.pupil.paidAmount);
        assertEquals(50, locker.pupil.previoulyPaidAmount);
        assertFalse(locker.isOutOfOrder);
        assertTrue(locker.hasContract);

        assertEquals(locker.currentCodeIndex, 2);
        assertEquals(decrypt(locker.encryptedCodes[0], superUserKey), "111111");
        assertEquals(decrypt(locker.encryptedCodes[1], superUserKey), "222222");
        assertEquals(decrypt(locker.encryptedCodes[2], superUserKey), "333333");
        assertEquals(decrypt(locker.encryptedCodes[3], superUserKey), "444444");
        assertEquals(decrypt(locker.encryptedCodes[4], superUserKey), "555555");
    }

    @Test
    public void shouldHaveCorrectDataForLocker2() {
        Locker locker = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(0).getLockerCabinet().lockers.get(1);
        assertNotNull(locker.encryptedCodes);

        assertEquals("Don", locker.pupil.lastName);
        assertEquals("Draper", locker.pupil.firstName);
        assertEquals("01.01.2021", locker.pupil.rentedFromDate);
        assertEquals("31.12.2022", locker.pupil.rentedUntilDate);
        assertEquals("12", locker.pupil.schoolClassName);
        assertTrue(locker.isOutOfOrder);

        assertEquals(locker.currentCodeIndex, 0);
        assertEquals(decrypt(locker.encryptedCodes[0], superUserKey), "111111");
        assertEquals(decrypt(locker.encryptedCodes[1], superUserKey), "222222");
        assertEquals(decrypt(locker.encryptedCodes[2], superUserKey), "987654");
        assertEquals(decrypt(locker.encryptedCodes[3], superUserKey), "000000");
        assertEquals(decrypt(locker.encryptedCodes[4], superUserKey), "000000");
    }

    @Test
    public void shouldHaveCorrectDataForLocker3() {
        Locker locker = buildings.get(0).floors.get(0).walks.get(0).moduleWrappers.get(0).getLockerCabinet().lockers.get(2);

        assertNull(locker.pupil);

        assertFalse(locker.isOutOfOrder);
        assertFalse(locker.hasContract);

        assertNull(locker.encryptedCodes);
    }

    @Test
    public void tasksShouldMatch() {
        assertEquals("This is the 1st task!", tasks.get(0).description);
        assertTrue(tasks.get(0).isDone);

        assertEquals("This is the 2nd task!", tasks.get(1).description);
        assertFalse(tasks.get(1).isDone);
    }
}