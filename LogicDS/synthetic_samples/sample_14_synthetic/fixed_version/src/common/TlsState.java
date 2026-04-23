package common;

public class TlsState {
    public int handshakeStage;
    public int flags;
    public boolean handshakeComplete;
    public boolean masterSecretDerived;

    public int SSL3_FLAGS_CCS_OK;

    public TlsState() {
        handshakeStage = 0;
        flags = 0;
        handshakeComplete = false;
        masterSecretDerived = false;
        SSL3_FLAGS_CCS_OK = 0x0080;
    }
}
