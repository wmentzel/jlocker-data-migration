package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.main.MainFrame;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * DataManager is a singleton class. There can only be one instance of this
 * class at any time and it has to be accessed from anywhere. This may not be
 * the best design but it stays that way for the time being.
 */
public class DataManager {

    final private static DataManager instance = new DataManager();

    public static DataManager getInstance() {
        return instance;
    }

    /**
     * TODO remove
     * instance of the MainFrame object is need to call the setStatusMessage method
     */
    MainFrame mainFrame;

    final public static boolean ERROR = true;

    private boolean hasDataChanged;

    private File resourceFile;
    private File backupDirectory;

    private List<Building> buildings;
    private List<User> users;
    private List<Task> tasks;
    private TreeMap settings;

    private SealedObject sealedBuildingsObject;

    private int currentBuildingIndex;
    private int currentFloorIndex;
    private int currentWalkIndex;
    private int currentColumnIndex;
    private int currentLockerIndex;
    private int currentUserIndex;

    private ResourceBundle bundle = ResourceBundle.getBundle("App");

    public DataManager() {
        currentBuildingIndex = 0;
        currentFloorIndex = 0;
        currentWalkIndex = 0;
        currentColumnIndex = 0;
        currentLockerIndex = 0;
        currentUserIndex = 0;

        hasDataChanged = false;

        buildings = new LinkedList<>();

        determineAppDir();
    }

    /* *************************************************************************
        Load and save methods
    ***************************************************************************/

    /**
     * Saves all data and creates a backup file with a time stamp.
     */
    public void saveAndCreateBackup() {

        saveData(resourceFile); // save to file jlocker.dat

        // Check if backup directory exists. If not, create it.
        if (!backupDirectory.exists() && !backupDirectory.mkdir()) {
            System.out.println("Backup failed!");
        }

        //
        // Check if a buildings.dat file exists to copy it to the backup directory.
        //
        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        File backupFile = new File(backupDirectory, String.format("jlocker-%04d-%02d-%02d.dat",
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)));

        // if a backup from this day doesnt exist, create one!
        if (!backupFile.exists()) {
            saveData(backupFile);
        }

        //
        // Just keep a certain number of last saved building files
        //
        if (backupDirectory.exists()) // if there are not backups yet, we dont have to delete any files
        {
            // This filter only returns files (and not directories)
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            };

            File[] files = backupDirectory.listFiles(fileFilter);

            Integer iNumBackups = (Integer) settings.get("NumOfBackups");

            for (int i = 0; i < files.length - iNumBackups; i++) {
                System.out.print("* delete backup file: \"" + files[i].getName() + "\"...");

                if (files[i].delete()) {
                    System.out.println(" successful!");
                } else {
                    System.out.println(" failed!");
                }
            }
        }
    }

    private void saveData(File file) {

        System.out.print("* saving " + file.getName() + "... ");

        try {
            byte[] b = SecurityManager.serialize(buildings);
            sealedBuildingsObject = SecurityManager.encryptObject(b, users.get(0).getUserMasterKey());

            try (
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos)
            ) {

                oos.writeObject(users);
                oos.writeObject(sealedBuildingsObject);
                oos.writeObject(tasks);
                oos.writeObject(settings);

                System.out.println("successful");
                mainFrame.setStatusMessage("Speichern erfolgreich");
            }
        } catch (Exception ex) {
            System.out.println("failed");
            mainFrame.setStatusMessage("Speichern fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    public void loadDefaultFile() {
        loadFromCustomFile(resourceFile);
    }

    /**
     * Loads the data from an arbitry file path and initializes the users,
     * buildings, tasks and settings objects. When called directly this is used
     * to load backup files. If you want to load the current "jlocker.dat" file
     * please use loadData() method instead.
     */
    public void loadFromCustomFile(File file) {

        System.out.print("* reading " + file.getName() + "... ");

        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            users = (List<User>) ois.readObject();
            sealedBuildingsObject = (SealedObject) ois.readObject();
            tasks = (LinkedList<Task>) ois.readObject();
            settings = (TreeMap) ois.readObject();

            System.out.println("successful");
            mainFrame.setStatusMessage("Laden erfolgreich");
        } catch (Exception ex) {
            System.out.println("failed");
            mainFrame.setStatusMessage("Laden fehlgeschlagen");
            ex.printStackTrace();
        }
    }

    /**
     * When there was no settings object loaded, it is created by this method
     * with default values.
     */
    public void loadDefaultSettings() {
        settings = new TreeMap();
        settings.put("LockerOverviewFontSize", 20);
        settings.put("NumOfBackups", 10);

        List<Integer> iMinSizes = new LinkedList<>();

        iMinSizes.add(0); // size for bottom locker
        iMinSizes.add(0);
        iMinSizes.add(140);
        iMinSizes.add(150);
        iMinSizes.add(175); // size for top locker

        settings.put("LockerMinSizes", iMinSizes);
    }
    
    /* *************************************************************************
        Getter
    ***************************************************************************/

    public Locker getLockerByID(String id) {
        for (Building building : buildings) {
            List<Floor> floors = building.getFloorList();

            for (Floor floor : floors) {
                List<Walk> walks = floor.getWalkList();

                for (Walk walk : walks) {
                    List<ManagementUnit> mus = walk.getManagementUnitList();

                    for (ManagementUnit mu : mus) {
                        List<Locker> lockers = mu.getLockerList();

                        for (Locker locker : lockers) {
                            if (locker.getId().equals(id)) {
                                return locker;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determines whether the given name is already assigned to a building.
     */
    public boolean isBuildingNameUnique(String name) {
        int iSize = buildings.size();

        for (int i = 0; i < iSize; i++) {
            if (((Building) buildings.get(i)).getName().equals(name))
                return false;
        }

        return true;
    }

    /**
     * Moves a student from one locker to another.
     */
    public void moveLockers(Locker sourceLocker, Locker destLocker, boolean withCodes) throws CloneNotSupportedException {
        Locker destCopy = destLocker.getCopy();

        destLocker.setTo(sourceLocker);
        sourceLocker.setTo(destCopy);

        if (withCodes) {
            SecretKey key = getCurUser().getSuperUMasterKey();

            destLocker.setCodes(sourceLocker.getCodes(key), key);
            sourceLocker.setCodes(destCopy.getCodes(key), key);
        }
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public String getAppTitle() {
        return bundle.getString("Application.title");
    }

    public String getAppVersion() {
        return bundle.getString("Application.version");
    }

    public TreeMap getSettings() {
        return settings;
    }

    public File getRessourceFile() {
        return resourceFile;
    }

    public File getBackupDirectory() {
        return backupDirectory;
    }

    public boolean isLockerIdUnique(String id) {
        return getLockerByID(id) == null;
    }

    public SealedObject getSealedBuildingsObject() {
        return sealedBuildingsObject;
    }

    public User getCurUser() {
        return users.get(currentUserIndex);
    }

    public List<User> getUserList() {
        return users;
    }

    public List<Building> getBuildingList() {
        return buildings;
    }

    public int getCurBuildingIndex() {
        return currentBuildingIndex;
    }

    public Building getCurBuilding() {
        return buildings.get(currentBuildingIndex);
    }

    public List<Floor> getCurFloorList() {
        return getCurBuilding().getFloorList();
    }

    public Floor getCurFloor() {
        return getCurFloorList().get(currentFloorIndex);
    }

    public int getCurFloorIndex() {
        return currentFloorIndex;
    }

    public List<Walk> getCurWalkList() {
        return getCurFloor().getWalkList();
    }

    public Walk getCurWalk() {
        return getCurWalkList().get(currentWalkIndex);
    }

    public int getCurWalkIndex() {
        return currentWalkIndex;
    }

    public List<ManagementUnit> getCurManagmentUnitList() {
        return getCurWalk().getManagementUnitList();
    }

    public ManagementUnit getCurManamentUnit() {
        return getCurManagmentUnitList().get(currentColumnIndex);
    }

    public int getCurManagementUnitIndex() {
        return currentColumnIndex;
    }

    public List<Locker> getCurLockerList() {
        return getCurManamentUnit().getLockerList();
    }

    public Locker getCurLocker() {
        return getCurLockerList().get(currentLockerIndex);
    }

    public int getCurLockerIndex() {
        return currentLockerIndex;
    }

    public Room getCurRoom() {
        return getCurManamentUnit().getRoom();
    }

    public LockerCabinet getCurLockerCabinet() {
        return getCurManamentUnit().getLockerCabinet();
    }

    public boolean hasDataChanged() {
        return hasDataChanged;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    /* *************************************************************************
        Setter
    ***************************************************************************/
    public void initBuildingObject() {
        this.buildings = SecurityManager.unsealAndDeserializeBuildings(
                getSealedBuildingsObject(), getUserList().get(0).getUserMasterKey()
        );
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setUserList(List<User> users) {
        this.users = users;
    }

    public void setDataChanged(boolean changed) {
        hasDataChanged = changed;
    }

    public void setCurrentBuildingIndex(int index) {
        currentBuildingIndex = index;
    }

    public void setCurrentFloorIndex(int index) {
        currentFloorIndex = index;
    }

    public void setCurrentWalkIndex(int index) {
        currentWalkIndex = index;
    }

    public void setCurrentMUnitIndex(int index) {
        currentColumnIndex = index;
    }

    public void setCurrentLockerIndex(int index) {
        currentLockerIndex = index;
    }

    public void setCurrentUserIndex(int index) {
        currentUserIndex = index;
    }

    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    /* *************************************************************************
        Private Methods
    ***************************************************************************/

    private void determineAppDir() {
        URL url = MainFrame.class.getProtectionDomain().getCodeSource().getLocation();
        File sHomeDir = new File(url.getFile());

        if (!sHomeDir.isDirectory()) {
            sHomeDir = sHomeDir.getParentFile();
        }

        resourceFile = new File(sHomeDir, "jlocker.dat");
        backupDirectory = new File(sHomeDir, "Backup");

        System.out.println("* program directory is: \"" + sHomeDir + "\"");
    }
}
