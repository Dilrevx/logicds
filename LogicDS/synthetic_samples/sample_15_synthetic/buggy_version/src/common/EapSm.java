package common;

public class EapSm {
    public SslData sslData;

    public EapSm() {
        sslData = new SslData();
    }

    public boolean tlsConnectionEstablished() {
        return sslData.connectionEstablished;
    }

    public boolean tlsConnectionResumed() {
        return sslData.sessionResumed;
    }
}
