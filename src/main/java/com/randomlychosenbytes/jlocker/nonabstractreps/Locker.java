package com.randomlychosenbytes.jlocker.nonabstractreps;

import javax.swing.*;

@SuppressWarnings("unused")
public class Locker extends JLabel implements java.io.Serializable, Cloneable {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 7707447616883782260L;

    public String sID;
    public String sSirName;
    public String sName;
    public int iSize;
    public String sClass;
    public String sFrom;
    public String sUntil;
    public boolean hasContract;
    public int iMoney;
    public int iPrevAmount;
    public boolean isOutOfOrder;
    public String sLock;
    public String sNote;
    public Boolean isSelected;

    private int iCurrentCodeIndex;
    private byte encCodes[][];
}
