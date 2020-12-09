import abstractreps.ManagementUnit;
import nonabstractreps.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import static junit.framework.TestCase.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class DataMigrationTest {

    public List<User> users;
    public List<Building> buildings;
    public List<Task> tasks;
    public TreeMap settings;

    public ManagementUnit mainManagementUnit;

    @Before
    public void setup() {

        File jlockerDatFile = new File(Util.getAppDir(), "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(jlockerDatFile, "11111111", "22222222");

        buildings = oldData.buildings;
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
        assertEquals(5, buildings.get(0).floors.get(0).walks.get(0).mus.size());
    }

    @Test
    public void numberOfLockersShouldBeCorrect() {
        assertEquals(3, buildings.get(0).floors.get(0).walks.get(0).mus.get(0).cabinet.lockers.size());
        assertEquals(3, buildings.get(0).floors.get(0).walks.get(0).mus.get(1).cabinet.lockers.size());
        assertEquals(3, buildings.get(0).floors.get(0).walks.get(0).mus.get(2).cabinet.lockers.size());
        assertEquals(3, buildings.get(0).floors.get(0).walks.get(0).mus.get(3).cabinet.lockers.size());
        assertEquals(3, buildings.get(0).floors.get(0).walks.get(0).mus.get(4).cabinet.lockers.size());
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
        assertEquals("ground floor", buildings.get(1).floors.get(0).sName);
    }

    @Test
    public void walkNamesShouldMatch() {
        assertEquals("main walk", buildings.get(0).floors.get(0).walks.get(0).sName);
        assertEquals("second walk", buildings.get(0).floors.get(0).walks.get(1).sName);
        assertEquals("-", buildings.get(0).floors.get(1).walks.get(0).sName);
        assertEquals("main walk", buildings.get(1).floors.get(0).walks.get(0).sName);
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
        assertEquals("12-34-56", locker.sLock);
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