package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    @Expose
    public Integer lockerOverviewFontSize = 20;

    @Expose
    public Integer numOfBackups = 10;

    @Expose
    public List<Integer> lockerMinSizes;

    public Settings() {
        List minSizes = new ArrayList<Integer>();
        minSizes.add(0);
        minSizes.add(0);
        minSizes.add(140);
        minSizes.add(150);
        minSizes.add(175);

        lockerMinSizes = minSizes;
    }
}
