import java.io.File;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(in);

        out.println("Super User password: ");
        String superUserPassword = scanner.nextLine();

        out.println("Restricted User password: ");
        String restrictedUserPassword = scanner.nextLine();

        File appDir = Util.getAppDir(true);
        out.println("* Program directory is: \"" + appDir.getAbsolutePath() + "\"");

        File oldJLockerDatFile = new File(appDir, "jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, restrictedUserPassword);

        File newJLockerDatFile = new File(appDir, "jlocker.json");

        NewData newData = NewFormatUtil.convert(oldData, superUserPassword, restrictedUserPassword, oldData.users.get(0).getSecretKeys(superUserPassword).first);

        com.randomlychosenbytes.jlocker.newformat.NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, restrictedUserPassword,
                newData.buildings,
                newData.settings,
                newData.tasks,
                newData.superUser,
                newData.restrictedUser
        );
        out.println("* File conversion successfully completed. The new file can be found here: \"" + newJLockerDatFile.getAbsolutePath() + "\"");
    }
}
