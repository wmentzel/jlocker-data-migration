package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;

import java.util.LinkedList;
import java.util.List;

public class Walk extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -4848128937225865954L;

    private List<ManagementUnit> mus;

    public Walk(String name) {
        sName = name;
        mus = new LinkedList<>();
    }

    public Walk() {
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/

    public void setMus(List<ManagementUnit> mus) {
        this.mus = mus;
    }

    public void setCurLockerIndex(Locker locker) {
        for (int m = 0; m < mus.size(); m++) {
            List<Locker> lockers = mus.get(m).getLockerList();

            int l;

            if ((l = lockers.indexOf(locker)) != -1) {
                DataManager dm = DataManager.getInstance();
                dm.setCurrentLockerIndex(l);
                dm.setCurrentMUnitIndex(m);
            }
        }
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public List<ManagementUnit> getManagementUnitList() {
        return mus;
    }

    public List<ManagementUnit> getMus() {
        return mus;
    }
}
