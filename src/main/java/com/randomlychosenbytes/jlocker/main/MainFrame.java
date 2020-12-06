package com.randomlychosenbytes.jlocker.main;

import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.manager.OldData;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.User;

import java.io.File;
import java.util.List;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class MainFrame extends javax.swing.JFrame {

    private static DataManager dataManager = DataManager.getInstance();

    public static void main(String args[]) {
        OldData oldData = dataManager.loadFromCustomFile(new File("/home/willi/code/jlocker/target/classes/jlocker.dat"));

        User superUser = oldData.users.get(0);

        superUser.isPasswordCorrect("11111111");

        List<Building> buildings = SecurityManager.unsealAndDeserializeBuildings(oldData.sealedBuildingsObject, oldData.users.get(0).getUserMasterKey());

        System.out.println(buildings.size());
    }
}
