package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.List;

public class LockerCabinet extends Module {

    @Expose
    public List<Locker> lockers;
}
