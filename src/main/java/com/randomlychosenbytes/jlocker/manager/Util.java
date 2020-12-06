package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.Main;

import java.io.File;
import java.net.URL;

public class Util {
    public static File getAppDir() {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        File codeRootDir = new File(url.getFile()).getParentFile().getParentFile();

        System.out.println("* program directory is: \"" + codeRootDir.getAbsolutePath() + "\"");

        return codeRootDir;
    }
}
