package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

public class Pupil {
    @Expose
    public String lastName;

    @Expose
    public String firstName;

    @Expose
    public int heightInCm;

    @Expose
    public int paidAmount;

    @Expose
    public int previoulyPaidAmount;

    @Expose
    public String rentedFromDate;

    @Expose
    public String rentedUntilDate;

    @Expose
    public String schoolClassName;
}
