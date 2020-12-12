package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Walk extends Entity {

    @Expose
    public List<ManagementUnit> managementUnits;
}
