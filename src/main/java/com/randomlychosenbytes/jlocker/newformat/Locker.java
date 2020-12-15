package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;

public class Locker extends JLabel implements Cloneable {

    @Expose
    public String id;

    @Expose
    public boolean hasContract;

    @Expose
    public boolean isOutOfOrder;

    @Expose
    public String lockCode;

    @Expose
    public String note;

    @Expose
    public int currentCodeIndex;

    @Expose
    public String encryptedCodes[];

    @Expose
    public Pupil pupil;

    public Boolean isSelected = false;
}
