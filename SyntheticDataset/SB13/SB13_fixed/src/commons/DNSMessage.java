package commons;

public class DNSMessage {
    public Object mctx;
    public int fromToWire;
    public Object tsig;
    public Object sig0;
    public int verifyAttempted;
    public int sig0status;
    public int tsigstatus;
    public TSIGKey tsigkey;
    public boolean verifiedSig;

    public static boolean isValid(DNSMessage msg) {
        return msg != null;
    }

    public static void takeBuffer(DNSMessage msg, Buffer buf) { }
}
