package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import javax.swing.*;

public class Locker extends JLabel implements Cloneable {

    @Expose
    public String id;

    @Expose
    public String lastName;

    @Expose
    public String firstName;

    @Expose
    public int sizeInCm;

    @Expose
    public String schoolClassName;

    @Expose
    public String rentedFromDate;

    @Expose
    public String rentedUntilDate;

    @Expose
    public boolean hasContract;

    @Expose
    public int paidAmount;

    @Expose
    public int previoulyPaidAmount;

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

    public Boolean isSelected = false;
}
