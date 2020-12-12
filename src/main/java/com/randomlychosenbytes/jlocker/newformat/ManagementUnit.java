package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;

public class ManagementUnit extends JPanel {

    @Expose
    public LockerCabinet lockerCabinet;

    @Expose
    public Room room;

    @Expose
    public Staircase staircase;

    /**
     * Can either be 0 (ROOM), 1 (LOCKERCOLUMN) or 2 (STAIRCASE)
     */
    @Expose
    public int type;

    public static final int ROOM = 0;
    public static final int LOCKERCOLUMN = 1;
    public static final int STAIRCASE = 2;
}
