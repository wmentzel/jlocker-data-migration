package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Building extends Entity {

    @Expose
    public String notes;

    @Expose
    public List<Floor> floors;
}
