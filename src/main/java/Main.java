import com.randomlychosenbytes.jlocker.newformat.NewFormatUtil;

import java.io.File;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class Main {

    public static void main(String[] args) {

        String superUserPassword = "11111111";
        String limitedUserPassword = "22222222";

        File appDir = Util.getAppDir();
        System.out.println("* program directory is: \"" + appDir.getAbsolutePath() + "\"");

        File oldJLockerDatFile = new File(appDir, "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, limitedUserPassword);

        File newJLockerDatFile = new File(appDir, "src/test/data/jlocker.json");

        NewData newData = Converter.convert(oldData, superUserPassword, limitedUserPassword, oldData.users.get(0).getSecretKeys(superUserPassword).getX());

        NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, limitedUserPassword,
                newData.buildings,
                newData.settings,
                newData.tasks,
                newData.superUser,
                newData.restrictedUser
        );
    }
}
