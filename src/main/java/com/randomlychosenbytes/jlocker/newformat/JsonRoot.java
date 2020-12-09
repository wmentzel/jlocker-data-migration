package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.annotations.Expose;

import java.util.List;

public class JsonRoot {

    public JsonRoot(
            String encryptedBuildingsBase64,
            Settings settings,
            List<Task> tasks,
            SuperUser superUser,
            RestrictedUser restrictedUser
    ) {
        this.encryptedBuildingsBase64 = encryptedBuildingsBase64;
        this.settings = settings;
        this.tasks = tasks;
        this.superUser = superUser;
        this.restrictedUser = restrictedUser;
    }

    @Expose
    public String encryptedBuildingsBase64;

    @Expose
    public Settings settings;

    @Expose
    public List<Task> tasks;

    @Expose
    public SuperUser superUser;

    @Expose
    public RestrictedUser restrictedUser;
}
