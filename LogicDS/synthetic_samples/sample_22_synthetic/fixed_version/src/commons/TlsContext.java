package commons;

public class TlsContext {
    public Buffers buffers = new Buffers();
    public Options options = new Options();
    public boolean peerEd25519KeyPresent;
    public boolean peerEd448KeyPresent;
    public boolean peerEccDsaKeyPresent;
    public boolean peerFalconKeyPresent;
    public Object peerRsaKey;
    public boolean peerRsaKeyPresent;
}