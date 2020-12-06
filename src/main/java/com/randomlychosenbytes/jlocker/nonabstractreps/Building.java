package com.randomlychosenbytes.jlocker.nonabstractreps;

import java.util.LinkedList;
import java.util.List;

public class Building extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -8221591221999653683L;

    private String notes;
    private List<Floor> floors;

    public Building(String name, String notes) {
        sName = name;
        this.notes = notes;
        floors = new LinkedList<>();
    }

    public Building(String name) {
        sName = name;
        this.notes = "";
        floors = new LinkedList<>();
    }

    public Building() {
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public String getNotes() {
        return notes;
    }

    /**
     * Determines whether or not the floor number is unique.
     */
    public boolean isFloorNameUnique(String name) {
        int iSize = floors.size();

        for (int i = 0; i < iSize; i++) {
            if (((Floor) floors.get(i)).getName().equals(name)) {
                return false;
            }
        }

        return true;
    }

    public List<Floor> getFloorList() {
        return floors;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
