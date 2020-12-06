import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.manager.OldData;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.Task;
import com.randomlychosenbytes.jlocker.nonabstractreps.User;
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

    @Before
    public void setup() {
        DataManager dataManager = DataManager.getInstance();

        OldData oldData = dataManager.loadFromCustomFile(new File("/home/willi/code/jlocker/target/classes/jlocker.dat"));

        User superUser = oldData.users.get(0);

        superUser.isPasswordCorrect("11111111");

        buildings = SecurityManager.unsealAndDeserializeBuildings(
                oldData.sealedBuildingsObject,
                oldData.users.get(0).getUserMasterKey()
        );

        users = oldData.users;
        tasks = oldData.tasks;
        settings = oldData.settings;
    }

    @Test
    public void numberOfBuildingsShouldBeCorrect() {
        assertEquals(1, buildings.size());
    }
}