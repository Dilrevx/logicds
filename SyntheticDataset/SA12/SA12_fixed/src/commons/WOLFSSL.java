package commons;

public class WOLFSSL {
    public Keys keys = new Keys();
    public Options options = new Options();
    public byte[] clientSecret;
    public byte[] serverSecret;

    public static class Keys {
        public int padSz;
        public byte[] client_write_MAC_secret;
        public byte[] server_write_MAC_secret;
    }

    public static class Options {
        public boolean handShakeDone;
        public int side;
        public boolean mutualAuth;
        public boolean failNoCert;
        public boolean havePeerVerify;
        public boolean resuming;
        public int serverState;
        public int clientState;
        public int handShakeState;
    }
}