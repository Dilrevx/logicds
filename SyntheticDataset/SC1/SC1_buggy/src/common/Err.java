package common;

public class Err {
    public static final int LIB_SSL = 1;
    public static final int R_MALLOC_FAILURE = 2;
    public static void raise(int lib, int reason) {}
    public static int ssl_x509err2alert(int err) { return 0; }
}
