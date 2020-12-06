package com.randomlychosenbytes.jlocker.nonabstractreps;

import java.util.List;

@SuppressWarnings("unused")
public class Floor extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 5477487009397387778L;

    private List<Walk> walks;

    public List<Walk> getWalkList() {
        return walks;
    }

    public List<Walk> getWalks() {
        return walks;
    }
}
