import java.io.File;

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
public class Main extends javax.swing.JFrame {

    public static void main(String args[]) {
        File jlockerDatFile = new File(Util.getAppDir(), "src/test/data/jlocker.dat");
        OldData oldData = OldFormatUtil.loadData(jlockerDatFile, "11111111", "22222222");
    }

}
