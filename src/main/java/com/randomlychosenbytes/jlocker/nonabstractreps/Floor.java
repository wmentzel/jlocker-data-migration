package com.randomlychosenbytes.jlocker.nonabstractreps;

import java.util.LinkedList;
import java.util.List;

public class Floor extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 5477487009397387778L;

    private List<Walk> walks;

    public Floor(String name) {
        sName = name;
        walks = new LinkedList<>();
    }

    public Floor() {
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/

    public void setWalks(List<Walk> walks) {
        this.walks = walks;
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public boolean isWalkNameUnique(String name) {
        int iSize = walks.size();

        for (int i = 0; i < iSize; i++) {
            if (((Walk) walks.get(i)).getName().equals(name))
                return false;
        }

        return true;
    }

    public List<Walk> getWalkList() {
        return walks;
    }

    public List<Walk> getWalks() {
        return walks;
    }
}
