package com.randomlychosenbytes.jlocker.nonabstractreps;

import java.util.List;

public class Building extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -8221591221999653683L;

    public String notes;
    public List<Floor> floors;
}
