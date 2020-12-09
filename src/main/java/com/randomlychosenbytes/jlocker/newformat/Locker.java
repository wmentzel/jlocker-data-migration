package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;

public class Locker extends JLabel implements Cloneable {

    @Expose
    public String sID;

    @Expose
    public String sSirName;

    @Expose
    public String sName;

    @Expose
    public int iSize;

    @Expose
    public String sClass;

    @Expose
    public String sFrom;

    @Expose
    public String sUntil;

    @Expose
    public boolean hasContract;

    @Expose
    public int iMoney;

    @Expose
    public int iPrevAmount;

    @Expose
    public boolean isOutOfOrder;

    @Expose
    public String sLock;

    @Expose
    public String sNote;

    @Expose
    public int iCurrentCodeIndex;

    public Boolean isSelected = false;

    @Expose
    public String encCodes[];
}
