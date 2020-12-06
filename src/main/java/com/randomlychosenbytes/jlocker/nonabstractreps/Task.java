package com.randomlychosenbytes.jlocker.nonabstractreps;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public Task(String description) {
        sDescription = description;
        isDone = false;

        Calendar today = new GregorianCalendar();

        sDate = String.format("%02d.%02d.%02d", today.get(Calendar.DATE),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.YEAR));
    }

    public Task() {
    }

    /* *************************************************************************
        Getter
    ***************************************************************************/
    public String getSDate() {
        return sDate;
    }

    public String getSDescription() {
        return sDescription;
    }

    public boolean isDone() {
        return isDone;
    }

    /* *************************************************************************
        Setter
    ***************************************************************************/
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

