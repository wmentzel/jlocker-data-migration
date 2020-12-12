package com.randomlychosenbytes.jlocker.newformat;

import abstractreps.ManagementUnit;
import com.google.gson.annotations.Expose;
import nonabstractreps.Entity;

import java.util.List;

public class Walk extends Entity {

    @Expose
    public List<ManagementUnit> managementUnits;
}
