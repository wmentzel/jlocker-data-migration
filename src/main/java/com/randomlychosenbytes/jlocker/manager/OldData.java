package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.nonabstractreps.Task;
import com.randomlychosenbytes.jlocker.nonabstractreps.User;

import javax.crypto.SealedObject;
import java.util.List;
import java.util.TreeMap;

public class OldData {

    public List<User> users;
    public SealedObject sealedBuildingsObject;
    public List<Task> tasks;
    public TreeMap settings;
}
