import java.io.Console;
import java.io.File;

import static java.lang.System.out;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class Main {

    public static void main(String[] args) {

        Console console = System.console();

        if (console == null) {
            System.err.println("Error: Please run this app in a proper terminal.");
            return;
        }

        out.println("Super User password: ");
        String superUserPassword = new String(console.readPassword());

        out.println("Restricted User password: ");
        String restrictedUserPassword = new String(console.readPassword());

        File appDir = Util.getAppDir(true);

        File oldJLockerDatFile = new File(appDir, "jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, restrictedUserPassword);

        File newJLockerDatFile = new File(appDir, "jlocker.json");

        NewData newData = NewFormatUtil.convert(oldData, superUserPassword, restrictedUserPassword, oldData.users.get(0).getSecretKeys(superUserPassword).first);

        NewFormatUtil.saveData(
                newJLockerDatFile, superUserPassword, restrictedUserPassword,
                newData.buildings,
                newData.settings,
                newData.tasks,
                newData.superUser,
                newData.restrictedUser
        );
        out.println("File conversion successfully completed. The new file can be found here: \"" + newJLockerDatFile.getAbsolutePath() + "\"");
    }
}
