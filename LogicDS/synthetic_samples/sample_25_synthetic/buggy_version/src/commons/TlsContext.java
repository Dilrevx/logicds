package commons;

public class TlsContext {
    public Options options = new Options();
    public Buffers buffers = new Buffers();
    public Keys keys       = new Keys();

    public boolean peerEd25519KeyPresent;
    public boolean peerEd448KeyPresent;
    public boolean peerEccDsaKeyPresent;
    public boolean peerRsaKeyPresent;
}
