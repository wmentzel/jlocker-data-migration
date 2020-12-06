package com.randomlychosenbytes.jlocker.nonabstractreps;

public class Staircase extends javax.swing.JPanel {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 0L;

    private String sName;

    public Staircase() {
            }

    /* *************************************************************************
     * Setter
     **************************************************************************/
    public void setEntityName(String n) {
        sName = n;
        captionLabel.setText("<html><div align=\"center\">Treppenhaus<br><br><div style='font-size:12pt;'>" + sName + "</div></div></html>");
    }

    public void setSName(String n) {
        sName = n;
    }

    public void setUpMouseListener() {
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public String getSName() {
        return sName;
    }

    public String getEntityName() {
        return sName;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel captionLabel;
    // End of variables declaration//GEN-END:variables
}
