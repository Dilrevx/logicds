package common;

public class EapPeapData {
    public static final int NO_AUTH = 0;
    public static final int FOR_INITIAL = 1;
    public static final int ALWAYS = 2;

    public int phase2Auth = FOR_INITIAL;
    public boolean phase2EapStarted = false;
    public boolean phase2EapSuccess = false;
    public SslData ssl;

    public EapPeapData(SslData ssl) {
        this.ssl = ssl;
    }
}
