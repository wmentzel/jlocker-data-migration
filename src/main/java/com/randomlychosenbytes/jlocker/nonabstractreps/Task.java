package com.randomlychosenbytes.jlocker.nonabstractreps;

@SuppressWarnings("unused")
public class Task implements java.io.Serializable {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -8372739826135250943L;

    private String sDescription;
    private boolean isDone;
    private String sDate;

    public Task() {
    }

    public String getSDate() {
        return sDate;
    }

    public String getSDescription() {
        return sDescription;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setSDate(String sDate) {
        this.sDate = sDate;
    }

    public void setSDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
}

