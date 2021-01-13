import com.randomlychosenbytes.jlocker.model.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.List;

import static com.randomlychosenbytes.jlocker.utils.CryptoKt.decrypt;
import static com.randomlychosenbytes.jlocker.utils.CryptoKt.decryptKeyWithString;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class DataMigrationTest {

    public static List<Building> buildings;
    public static List<Task> tasks;
    public static Settings settings;

    private static SecretKey superUserKey;

    @BeforeClass
    public static void setup() {

        System.getProperty("java.version");

        String superUserPassword = "11111111";
        String restrictedUserPassword = "22222222";

        File appDir = Util.getAppDir(false);

        File jlockerDatFile = new File(appDir, "../../src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(jlockerDatFile, "11111111", "22222222");

        NewData newData = NewFormatUtil.convert(oldData, superUserPassword, restrictedUserPassword, oldData.users.get(0).getSecretKeys(superUserPassword).first);

        File newJLockerDatFile = new File(appDir, "../../src/test/data/jlocker.json");

        NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, restrictedUserPassword,
                newData.buildings,
                newData.settings,
                newData.tasks,
                newData.superUser,
                newData.restrictedUser
        );

        NewData newDataLoadedFromFile = NewFormatUtil.loadFromCustomFile(newJLockerDatFile, superUserPassword, restrictedUserPassword);

        superUserKey = decryptKeyWithString(newDataLoadedFromFile.superUser.getEncryptedSuperUMasterKeyBase64(), superUserPassword);

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
        assertEquals(2, buildings.get(0).getFloors().size());
        assertEquals(1, buildings.get(1).getFloors().size());
    }

    @Test
    public void numberOfWalksShouldBeCorrect() {
        assertEquals(2, buildings.get(0).getFloors().get(0).getWalks().size());
        assertEquals(1, buildings.get(0).getFloors().get(1).getWalks().size());
        assertEquals(1, buildings.get(1).getFloors().get(0).getWalks().size());
    }

    @Test
    public void numberOfManagementUnitsShouldBeCorrect() {
        assertEquals(5, buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().size());
    }

    @Test
    public void numberOfLockersShouldBeCorrect() {
        List<ModuleWrapper> moduleWrappers = buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers();
        assertEquals(3, ((LockerCabinet) moduleWrappers.get(2).getModule()).getLockers().size());
        assertEquals(3, ((LockerCabinet) moduleWrappers.get(1).getModule()).getLockers().size());
        assertEquals(3, ((LockerCabinet) moduleWrappers.get(0).getModule()).getLockers().size());
    }

    @Test
    public void buildingNamesShouldMatch() {
        assertEquals("main building", buildings.get(0).getName());
        assertEquals("second building", buildings.get(1).getName());
    }

    @Test
    public void floorNamesShouldMatch() {
        assertEquals("ground floor", buildings.get(0).getFloors().get(0).getName());
        assertEquals("1st floor", buildings.get(0).getFloors().get(1).getName());
        assertEquals("ground floor", buildings.get(1).getFloors().get(0).getName());
    }

    @Test
    public void walkNamesShouldMatch() {
        assertEquals("main walk", buildings.get(0).getFloors().get(0).getWalks().get(0).getName());
        assertEquals("second walk", buildings.get(0).getFloors().get(0).getWalks().get(1).getName());
        assertEquals("-", buildings.get(0).getFloors().get(1).getWalks().get(0).getName());
        assertEquals("main walk", buildings.get(1).getFloors().get(0).getWalks().get(0).getName());
    }

    @Test
    public void lockerIdsShouldMatch() {
        LockerCabinet cabinet = (LockerCabinet) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(0).getModule();
        assertEquals("1", cabinet.getLockers().get(0).getId());
        assertEquals("2", cabinet.getLockers().get(1).getId());
        assertEquals("3", cabinet.getLockers().get(2).getId());
    }

    @Test
    public void roomDataShouldMatch() {
        Room room = (Room) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(3).getModule();

        assertEquals(room.getName(), "Some Classroom");
        assertEquals(room.getSchoolClassName(), "12");
    }

    @Test
    public void stairCaseDataShouldMatch() {
        Staircase staircase = (Staircase) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(4).getModule();
        assertEquals(staircase.getName(), "Main Staircase");
    }

    @Test
    public void lockerTypesShouldMatch() {
        List<ModuleWrapper> moduleWrappers = buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers();

        assertEquals(moduleWrappers.get(0).getModule().getClass().getSimpleName(), LockerCabinet.class.getSimpleName());
        assertEquals(moduleWrappers.get(1).getModule().getClass().getSimpleName(), LockerCabinet.class.getSimpleName());
        assertEquals(moduleWrappers.get(2).getModule().getClass().getSimpleName(), LockerCabinet.class.getSimpleName());

        assertEquals(moduleWrappers.get(3).getModule().getClass().getSimpleName(), Room.class.getSimpleName());
        assertEquals(moduleWrappers.get(4).getModule().getClass().getSimpleName(), Staircase.class.getSimpleName());
    }

    @Test
    public void shouldHaveCorrectDataForLocker1() {
        Locker locker = ((LockerCabinet) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(0).getModule()).getLockers().get(0);
        assertEquals("Olsen", locker.getPupil().getLastName());
        assertEquals("Peggy", locker.getPupil().getFirstName());
        assertEquals("11", locker.getPupil().getSchoolClassName());
        assertEquals(200, locker.getPupil().getHeightInCm());
        assertEquals("This is some note!", locker.getNote());
        assertEquals("01.01.2020", locker.getPupil().getRentedFromDate());
        assertEquals("01.01.2021", locker.getPupil().getRentedUntilDate());
        assertEquals(150, locker.getPupil().getPaidAmount());
        assertEquals(50, locker.getPupil().getPreviouslyPaidAmount());
        assertTrue(locker.getPupil().getHasContract());
        assertEquals("12-34-56", locker.getLockCode());
        assertFalse(locker.isOutOfOrder());

        assertEquals(locker.getCurrentCodeIndex(), 2);
        assertEquals("111111", decrypt(locker.getEncryptedCodes()[0], superUserKey));
        assertEquals("222222", decrypt(locker.getEncryptedCodes()[1], superUserKey));
        assertEquals("333333", decrypt(locker.getEncryptedCodes()[2], superUserKey));
        assertEquals("444444", decrypt(locker.getEncryptedCodes()[3], superUserKey));
        assertEquals("555555", decrypt(locker.getEncryptedCodes()[4], superUserKey));
    }

    @Test
    public void shouldHaveCorrectDataForLocker2() {
        Locker locker = ((LockerCabinet) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(0).getModule()).getLockers().get(1);
        assertNotNull(locker.getEncryptedCodes());

        assertEquals("Draper", locker.getPupil().getLastName());
        assertEquals("Don", locker.getPupil().getFirstName());
        assertEquals("01.01.2021", locker.getPupil().getRentedFromDate());
        assertEquals("31.12.2022", locker.getPupil().getRentedUntilDate());
        assertEquals("12", locker.getPupil().getSchoolClassName());
        assertTrue(locker.isOutOfOrder());

        assertEquals(locker.getCurrentCodeIndex(), 0);
        assertEquals("111111", decrypt(locker.getEncryptedCodes()[0], superUserKey));
        assertEquals("222222", decrypt(locker.getEncryptedCodes()[1], superUserKey));
        assertEquals("987654", decrypt(locker.getEncryptedCodes()[2], superUserKey));
        assertEquals("000000", decrypt(locker.getEncryptedCodes()[3], superUserKey));
        assertEquals("000000", decrypt(locker.getEncryptedCodes()[4], superUserKey));
    }

    @Test
    public void shouldHaveCorrectDataForLocker3() {
        Locker locker = ((LockerCabinet) buildings.get(0).getFloors().get(0).getWalks().get(0).getModuleWrappers().get(0).getModule()).getLockers().get(2);

        assertTrue(locker.isFree());
        assertFalse(locker.isOutOfOrder());
        assertNull(locker.getEncryptedCodes());
    }

    @Test
    public void tasksShouldMatch() {
        assertEquals("This is the 1st task!", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());

        assertEquals("This is the 2nd task!", tasks.get(1).getDescription());
        assertFalse(tasks.get(1).isDone());
    }
}
