package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

public class Task {

    @Expose
    public String sDescription;

    @Expose
    public boolean isDone = false;

    @Expose
    public String sDate;
}

