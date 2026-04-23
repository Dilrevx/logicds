package commons;

public class ERR {
    public static void raise(String errorCode) {
        System.err.println("ERROR: " + errorCode);
    }
}
