import java.io.File;
import java.net.URL;

public class Util {
    public static File getAppDir() {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        File codeRootDir = new File(url.getFile()).getParentFile().getParentFile();
        return codeRootDir;
    }
}
