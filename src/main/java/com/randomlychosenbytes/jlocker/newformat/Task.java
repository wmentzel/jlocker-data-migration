package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

public class Task {

    @Expose
    public String description;

    @Expose
    public boolean isDone = false;

    @Expose
    public String creationDate;
}

