package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;

public class ModuleWrapper extends JPanel {
    @Expose
    public Module module;

    public Room getRoom() {
        return (Room) module;
    }

    public Staircase getStaircase() {
        return (Staircase) module;
    }

    public LockerCabinet getLockerCabinet() {
        return (LockerCabinet) module;
    }
}
