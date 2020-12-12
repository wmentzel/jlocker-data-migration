package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Floor extends Entity {

    @Expose
    public List<Walk> walks;
}
