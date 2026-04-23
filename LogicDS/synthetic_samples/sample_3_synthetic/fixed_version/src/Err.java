public class Err {
    public static void raise(String lib, String msg) {
        System.err.println("[" + lib + "] Error: " + msg);
    }
}
