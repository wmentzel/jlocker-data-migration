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
    public int mType;
}
