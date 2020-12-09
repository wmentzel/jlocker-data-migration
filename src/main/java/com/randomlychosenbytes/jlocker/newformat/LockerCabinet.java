package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;
import java.util.List;

public class LockerCabinet extends JPanel {

    @Expose
    public List<Locker> lockers;
}
