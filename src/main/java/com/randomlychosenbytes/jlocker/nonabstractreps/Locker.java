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

    private String sID;
    private String sSirName;
    private String sName;
    private int iSize;
    private String sClass;
    private String sFrom;
    private String sUntil;
    private boolean hasContract;
    private int iMoney;
    private int iPrevAmount;
    private boolean isOutOfOrder;
    private String sLock;
    private String sNote;
    private Boolean isSelected;

    private int iCurrentCodeIndex;
    private byte encCodes[][];

    public String getNote() {
        return sNote;
    }

    public boolean isFree() {
        return sName.equals("");
    }
}
