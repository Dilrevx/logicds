package commons;

public class ClientSession {
    public boolean is_trivial_auth = false;
    public int state;
    public int lastauthtype;

    public static final int USERAUTH_SUCCESS_RCVD = 1;
    public static final int AUTH_TYPE_NONE = 0;

    public static void cli_auth_pubkey_cleanup() {
    }
}
