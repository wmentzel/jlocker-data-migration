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
public class DataManager {

    public static OldData loadFromCustomFile(File file) {

        System.out.print("* reading " + file.getName() + "... ");

        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            OldData oldData = new OldData();
            oldData.users = (List<User>) ois.readObject();
            oldData.sealedBuildingsObject = (SealedObject) ois.readObject();
            oldData.tasks = (LinkedList<Task>) ois.readObject();
            oldData.settings = (TreeMap) ois.readObject();

            System.out.println("successful");
            return oldData;

        } catch (Exception ex) {
            System.out.println("failed");
            ex.printStackTrace();
            return null;
        }
    }
}
