package nonabstractreps;

public class Entity implements java.io.Serializable {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = 8606236005570624210L;

    public String sName;
}
