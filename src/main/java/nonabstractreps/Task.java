package nonabstractreps;

public class Task implements java.io.Serializable {
    /**
     * If the object is manipulated another serialVersionUID will be assigned
     * by the compiler, even for minor changes. To avoid that it is set
     * by the programmer.
     */
    private static final long serialVersionUID = -8372739826135250943L;

    public String sDescription;
    public boolean isDone;
    public String sDate;
}

