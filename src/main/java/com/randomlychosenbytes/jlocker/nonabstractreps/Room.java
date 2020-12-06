package com.randomlychosenbytes.jlocker.nonabstractreps;

public class Room extends javax.swing.JPanel {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -859301949546702964L;

    private String sClass;
    private String sName;

    public Room(String name, String classname) {
        setCaption(name, classname);
    }

    public Room() {
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/

    public final void setCaption(String name, String classname) {
        sName = name;
        sClass = classname;

        String caption = "<html><div align=\"center\">" + sName;

        // if there was a class name specified
        if (!sClass.equals("")) {
            caption += "<br><br><div style='font-size:12pt;'>Klasse<br>" + sClass + "</div></div></html>";
        }

        captionLabel.setText(caption);
    }

    public void setRoomName(String newname) {
        sName = newname;
    }

    public void setSName(String newname) {
        sName = newname;
    }

    public void setSClass(String newname) {
        sClass = newname;
    }

    public void setUpMouseListener() {
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public String getSName() {
        return sName;
    }

    public String getSClass() {
        return sClass;
    }

    public String getRoomName() {
        return sName;
    }

    public String getClassName() {
        return sClass;
    }

    public void setClassName(String classname) {
        sClass = classname;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel captionLabel;
    // End of variables declaration//GEN-END:variables
}