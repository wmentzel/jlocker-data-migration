package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.swing.*;

public abstract class Module extends JPanel {

    @SerializedName("type")
    @Expose
    private String typeName;

    public Module() {
        typeName = getClass().getName();
    }
}
