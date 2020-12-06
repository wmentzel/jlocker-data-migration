import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.OldData;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import static com.randomlychosenbytes.jlocker.manager.DataManager.loadFromCustomFile;
import static com.randomlychosenbytes.jlocker.manager.Util.getAppDir;
import static junit.framework.TestCase.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class DataMigrationTest {

    public List<User> users;
    public List<Building> buildings;
    public List<Task> tasks;
    public TreeMap settings;

    public ManagementUnit mainManagementUnit;

    @Before
    public void setup() {

        File jlockerDatFile = new File(getAppDir(), "src/test/data/jlocker.dat");

        OldData oldData = loadFromCustomFile(jlockerDatFile);

        assertNotNull("Could not load jlocker.dat", oldData);

        assertEquals("There should be exactly two users.", oldData.users.size(), 2);

        User superUser = oldData.users.get(0);
        User limitedUser = oldData.users.get(1);

        assertTrue("Super user passwort does not match", superUser.isPasswordCorrect("11111111"));
        assertTrue("Limited user passwort does not match", limitedUser.isPasswordCorrect("22222222"));

        buildings = SecurityManager.unsealAndDeserializeBuildings(
                oldData.sealedBuildingsObject,
                User.decUserMasterKey
        );

        assertNotNull("Could not decrypt buildings with user password", buildings);

        users = oldData.users;
        tasks = oldData.tasks;
        settings = oldData.settings;

        mainManagementUnit = buildings.get(0).floors.get(0).walks.get(0).mus.get(4);
    }

    @Test
    public void numberOfBuildingsShouldBeCorrect() {
        assertEquals(2, buildings.size());
    }

    @Test
    public void buildingNamesShouldMatch() {
        assertEquals("main building", buildings.get(0).sName);
        assertEquals("second building", buildings.get(1).sName);
    }

    @Test
    public void floorNamesShouldMatch() {
        assertEquals("ground floor", buildings.get(0).floors.get(0).sName);
        assertEquals("1st floor", buildings.get(0).floors.get(1).sName);
        assertEquals("-", buildings.get(1).floors.get(0).sName);
    }

    @Test
    public void walkNamesShouldMatch() {
        assertEquals("main walk", buildings.get(0).floors.get(0).walks.get(0).sName);
        assertEquals("-", buildings.get(1).floors.get(0).walks.get(0).sName);
    }

    @Test
    public void lockerIdsShouldMatch() {
        LockerCabinet cabinet = mainManagementUnit.cabinet;
        assertEquals("1", cabinet.lockers.get(0).sID);
        assertEquals("2", cabinet.lockers.get(1).sID);
        assertEquals("3", cabinet.lockers.get(2).sID);
    }

    @Test
    public void lockerDataShouldMatch() {
        Locker locker = mainManagementUnit.cabinet.lockers.get(0);
        assertEquals("Lastname", locker.sSirName);
        assertEquals("Firstname", locker.sName);
        assertEquals("1", locker.sClass);
        assertEquals(200, locker.iSize);
        assertEquals("This is some note!", locker.sNote);
        assertEquals("01.01.2020", locker.sFrom);
        assertEquals("01.01.2021", locker.sUntil);
        assertEquals("12345", locker.sLock);
        assertEquals(150, locker.iMoney);
        assertEquals(50, locker.iPrevAmount);
        assertEquals(false, locker.isOutOfOrder);
        assertEquals(true, locker.hasContract);
    }

    @Test
    public void tasksShouldMatch() {
        assertEquals("This is the 1st task!", tasks.get(0).sDescription);
        assertEquals(true, tasks.get(0).isDone);

        assertEquals("This is the 2nd task!", tasks.get(1).sDescription);
        assertEquals(false, tasks.get(1).isDone);
    }
}