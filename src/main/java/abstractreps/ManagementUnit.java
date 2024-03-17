package abstractreps;

import nonabstractreps.LockerCabinet;
import nonabstractreps.Room;
import nonabstractreps.Staircase;

@SuppressWarnings("unused")
public class ManagementUnit extends javax.swing.JPanel {
    private static final long serialVersionUID = -8054374141198601700L;

    public LockerCabinet cabinet;
    public Room room;
    public Staircase staircase;

    public int mType;
    public static final int ROOM = 0;
    public static final int LOCKERCOLUMN = 1;
    public static final int STAIRCASE = 2;
}
