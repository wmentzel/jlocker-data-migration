package nonabstractreps;

public class User extends Entity {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -6899339135756518502L;

    public String sHash;
    public boolean isSuperUser;

    public byte[] encUserMasterKey;
    public byte[] encSuperUMasterKey;
}
