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

        File oldJLockerDatFile = new File(Util.getAppDir(), "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, limitedUserPassword);

        File newJLockerDatFile = new File(Util.getAppDir(), "src/test/data/new_jlocker.dat");

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
