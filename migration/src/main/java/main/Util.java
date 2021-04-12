package main;

import main.MainKt;

import java.io.File;

public class Util {
    public static File getAppDir(boolean isRelease) {
        File file = new File(MainKt.class.getProtectionDomain().getCodeSource().getLocation().getFile());

        if (isRelease) {
            return file.getParentFile();
        } else {
            return file.getParentFile().getParentFile();
        }
    }
}
