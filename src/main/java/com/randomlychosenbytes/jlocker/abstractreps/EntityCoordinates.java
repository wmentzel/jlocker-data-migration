package com.randomlychosenbytes.jlocker.abstractreps;

/**
 * Every Entity (ManamgentUnit, Locker, Room etc.) has a unique sequence of numbers.
 * I called them coordinates. Often it's necessary to save this coordinates alongside
 * the actual object we are dealing with. For instance in the SearchFrame: When we
 * double click on a query result we are automatically brought to the belonging
 * locker. For this we need to set the drop down menus to the building
 * (1st coordinate), floor (2nd coordinate) and walk (3rd coordinate) the locker
 * is in.
 */
public class EntityCoordinates<T> {
    T obj;
    int b, f, w, m, l;

    public EntityCoordinates(T t, int b, int f, int w, int m, int l) {
        obj = t;
        this.b = b;
        this.f = f;
        this.w = w;
        this.m = m;
        this.l = l;
    }

    public EntityCoordinates(int b, int f, int w, int m, int l) {
        this.b = b;
        this.f = f;
        this.w = w;
        this.m = m;
        this.l = l;
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/
    public int getBValue() {
        return b;
    }

    public int getFValue() {
        return f;
    }

    public int getWValue() {
        return w;
    }

    public int getMValue() {
        return m;
    }

    public int getLValue() {
        return l;
    }

    public T getEntity() {
        return obj;
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/
    public void setBValue(int b) {
        this.b = b;
    }

    public void setFValue(int f) {
        this.f = f;
    }

    public void setWValue(int w) {
        this.w = w;
    }

    public void setMValue(int m) {
        this.m = m;
    }

    public void setLValue(int l) {
        this.l = l;
    }
}
