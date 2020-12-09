import nonabstractreps.Building;
import nonabstractreps.SecurityManager;
import nonabstractreps.Task;
import nonabstractreps.User;

import javax.crypto.SealedObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 */
public class OldFormatUtil {

    private OldFormatUtil() {
    }

    public static OldData loadData(File jlockerDatFile, String superUserPassword, String limitedUserPassword) {

        List<User> users;
        SealedObject sealedBuildingsObject;
        List<Task> tasks;
        TreeMap settings;

        try (
                FileInputStream fis = new FileInputStream(jlockerDatFile);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            users = (List<User>) ois.readObject();
            sealedBuildingsObject = (SealedObject) ois.readObject();
            tasks = (LinkedList<Task>) ois.readObject();
            settings = (TreeMap) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        User superUser = users.get(0);
        User limitedUser = users.get(1);

        if (!superUser.isPasswordCorrect(superUserPassword)) {
            throw new RuntimeException("Super user passwort does not match");
        }

        if (!limitedUser.isPasswordCorrect(limitedUserPassword)) {
            throw new RuntimeException("Limited user passwort does not match");
        }

        List<Building> buildings = SecurityManager.unsealAndDeserializeBuildings(
                sealedBuildingsObject,
                User.decUserMasterKey
        );

        if (buildings == null) {
            throw new RuntimeException("Could not decrypt buildings with user password");
        }

        OldData oldData = new OldData();
        oldData.buildings = buildings;
        oldData.users = users;
        oldData.tasks = tasks;
        oldData.settings = settings;
        return oldData;
    }
}
