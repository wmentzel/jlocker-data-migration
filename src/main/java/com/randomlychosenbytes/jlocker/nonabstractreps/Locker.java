package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.DataManager;

import javax.crypto.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Locker extends JLabel implements java.io.Serializable, Cloneable {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 7707447616883782260L;

    private static final Color[] BACKGROUND_COLORS = new Color[]
            {
                    new Color(255, 0, 0),
                    new Color(0, 102, 0),
                    new Color(255, 255, 255),
                    new Color(255, 255, 0),
                    new Color(0, 0, 255),
                    new Color(255, 153, 0)
            };

    private static final Color[] FOREGROUND_COLORS = new Color[]
            {
                    new Color(255, 255, 255),
                    new Color(255, 255, 255),
                    new Color(0, 0, 0),
                    new Color(0, 0, 0),
                    new Color(255, 255, 255),
                    new Color(0, 0, 0)
            };

    public static final int OUTOFORDER_COLOR = 0;
    public static final int RENTED_COLOR = 1;
    public static final int FREE_COLOR = 2;
    public static final int SELECTED_COLOR = 3;
    public static final int NOCONTRACT_COLOR = 4;
    public static final int ONEMONTHREMAINING_COLOR = 5;

    private String sID;
    private String sSirName;
    private String sName;
    private int iSize;
    private String sClass;
    private String sFrom;
    private String sUntil;
    private boolean hasContract;
    private int iMoney;
    private int iPrevAmount;
    private boolean isOutOfOrder;
    private String sLock;
    private String sNote;
    private Boolean isSelected;

    private int iCurrentCodeIndex;
    private byte encCodes[][];

    public Locker(String id, String sirname, String name, int size,
                  String _class, String from, String until, boolean hascontract,
                  int money, int currentcodeindex, String lock,
                  boolean outoforder, String note) {
        sID = id;
        sSirName = sirname;
        sName = name;
        iSize = size;
        sClass = _class;
        sFrom = from;
        sUntil = until;
        hasContract = hascontract;
        iMoney = money;
        iPrevAmount = money;
        isOutOfOrder = outoforder;
        sLock = lock;
        sNote = note;
        isSelected = false;

        iCurrentCodeIndex = currentcodeindex;
        encCodes = null;

        // standard color
        setColor(FREE_COLOR);

        setText(sID);

        // If true the component paints every pixel within its bounds.
        setOpaque(true);

        // change font      
        setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        // font aligment
        setHorizontalAlignment(SwingConstants.CENTER);

        // assign mouse events
        setUpMouseListener();
    }

    /**
     * XMLEncoder
     */
    public Locker() {
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public String[] getCodes(SecretKey sukey) {
        String[] decCodes = new String[5];

        for (int i = 0; i < 5; i++) {
            decCodes[i] = getCode(i, sukey);
        }

        return decCodes;
    }

    private String getCode(int i, SecretKey sukey) {
        if (encCodes == null) {
            return "00-00-00";
        }

        try {
            Cipher dcipher = Cipher.getInstance("DES");
            dcipher.init(Cipher.DECRYPT_MODE, sukey);

            byte[] utf8 = dcipher.doFinal(encCodes[i]);
            String code = new String(utf8, "UTF8");

            return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(null, "Locker.getCode: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return null;
        }
    }


    public long getRemainingTimeInMonths() {
        if (sUntil.equals("") || sFrom.equals("") || isFree()) {
            return 0;
        }

        Calendar today = new GregorianCalendar();
        today.setLenient(false);
        today.getTime();

        int iDay = Integer.parseInt(sUntil.substring(0, 2));
        int iMonth = Integer.parseInt(sUntil.substring(3, 5)) - 1;
        int iYear = Integer.parseInt(sUntil.substring(6, 10));

        Calendar end = new GregorianCalendar(iYear, iMonth, iDay);
        end.setLenient(false);
        end.getTime();

        long iDifferenceInMonths = Math.round(((double) end.getTimeInMillis() - today.getTimeInMillis()) / 2592000000.0); // 2592000000.0 = 24 * 60 * 60 * 1000 * 30

        if (iDifferenceInMonths < 0) {
            return 0;
        }

        return iDifferenceInMonths;
    }

    public static boolean isDateValid(String date) {
        if (date.length() < 10) {
            return false;
        }

        try {
            // try to extract day, month and year
            int iDay = Integer.parseInt(date.substring(0, 2));
            int iMonth = Integer.parseInt(date.substring(3, 5)) - 1; // month is zero-based, january is 0... what the fuck!?
            int iYear = Integer.parseInt(date.substring(6, 10));

            Calendar c = new GregorianCalendar(iYear, iMonth, iDay);

            c.setLenient(false);
            c.getTime();

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public String getId() {
        return sID;
    }


    public String getSurname() {
        return sSirName;
    }


    public String getOwnerName() {
        return sName;
    }

    public int getOwnerSize() {
        return iSize;
    }

    public String getOwnerClass() {
        return sClass;
    }

    public String getFromDate() {
        return sFrom;
    }

    public String getUntilDate() {
        return sUntil;
    }

    public boolean hasContract() {
        return hasContract;
    }

    public int getMoney() {
        return iMoney;
    }

    public int getPrevAmount() {
        return iPrevAmount;
    }


    public int getCurrentCodeIndex() {
        return iCurrentCodeIndex;
    }


    public boolean isOutOfOrder() {
        return isOutOfOrder;
    }

    public String getLock() {
        return sLock;
    }

    public String getNote() {
        return sNote;
    }

    public boolean isFree() {
        return sName.equals("");
    }

    public String getCurrentCode(SecretKey sukey) {
        return getCode(iCurrentCodeIndex, sukey);
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public Locker getCopy() throws CloneNotSupportedException {
        return (Locker) this.clone();
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/

    public void setAppropriateColor() {
        if (hasContract) {
            setColor(RENTED_COLOR);
        } else {
            setColor(NOCONTRACT_COLOR);
        }

        if (getRemainingTimeInMonths() <= 1) {
            setColor(ONEMONTHREMAINING_COLOR);
        }

        if (isFree()) {
            setColor(FREE_COLOR);
        }

        if (isOutOfOrder) {
            setColor(OUTOFORDER_COLOR);
        }
    }

    public void setCodes(String codes[], SecretKey sukey) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.

        encCodes = new byte[5][];

        for (int i = 0; i < 5; i++) {
            codes[i] = codes[i].replace("-", "");
        }

        try {
            Cipher ecipher = Cipher.getInstance("DES");

            ecipher.init(Cipher.ENCRYPT_MODE, sukey);

            for (int i = 0; i < 5; i++) {
                byte[] utf8 = codes[i].getBytes("UTF8");
                encCodes[i] = ecipher.doFinal(utf8);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(null, "Locker.setCodes: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void empty() {
        sSirName = "";
        sName = "";
        iSize = 0;
        sClass = "";
        sFrom = "";
        sUntil = "";
        hasContract = false;
        iMoney = 0;
        iPrevAmount = 0;
        iCurrentCodeIndex = (iCurrentCodeIndex + 1) % encCodes.length;

        setAppropriateColor();
    }

    public void setTo(Locker newdata) {
        sSirName = newdata.sSirName;
        sName = newdata.sName;
        iSize = newdata.iSize;
        sClass = newdata.sClass;
        sFrom = newdata.sFrom;
        sUntil = newdata.sUntil;
        hasContract = newdata.hasContract;
        iMoney = newdata.iMoney;
        iPrevAmount = newdata.iPrevAmount;

        setAppropriateColor();
    }

    public void setID(String id) {
        setText(sID = id);
    }

    public void setSirName(String sirname) {
        sSirName = sirname;
    }

    public void setOwnerName(String name) {
        sName = name;
    }

    public void setOwnerSize(int size) {
        iSize = size;
    }

    public void setClass(String _class) {
        sClass = _class;
    }

    public void setFromDate(String fromdate) {
        sFrom = fromdate;
    }

    public void setUntilDate(String untildate) {
        sUntil = untildate;
    }

    public void setContract(boolean hascontract) {
        hasContract = hascontract;
    }

    public void setMoney(int money) {
        iMoney = money;
    }

    public void setPrevAmount(int amount) {
        iPrevAmount = amount;
    }

    public void setCurrentCodeIndex(int index) {
        iCurrentCodeIndex = index;
    }

    public void setNextCode() {
        iCurrentCodeIndex = (iCurrentCodeIndex + 1) % 5;
    }

    public void setOutOfOrder(boolean outoforder) {
        isOutOfOrder = outoforder;
    }

    public void setSelected() {
        isSelected = true;
        setColor(SELECTED_COLOR);
    }

    public void setLock(String lock) {
        sLock = lock;
    }

    public void setNote(String note) {
        sNote = note;
    }

    public final void setUpMouseListener() {
        if (this.getMouseListeners().length == 0) {
            addMouseListener(new MouseListener());
        }
    }

    public final void setColor(int index) {
        setBackground(BACKGROUND_COLORS[index]);
        setForeground(FOREGROUND_COLORS[index]);
    }

    /**
     * TODO move to MainFrame
     */
    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            DataManager dm = DataManager.getInstance();

            if (dm.getCurLockerList().size() > 0) {
                dm.getCurLocker().setAppropriateColor();
            }

            Locker locker = (Locker) e.getSource();
            dm.getCurWalk().setCurLockerIndex(locker);

            locker.setSelected();
            DataManager.getInstance().getMainFrame().showLockerInformation();
        }
    }
}
