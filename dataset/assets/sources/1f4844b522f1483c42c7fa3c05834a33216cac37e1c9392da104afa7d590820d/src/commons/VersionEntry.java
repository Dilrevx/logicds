package commons;

public class VersionEntry {
    private byte major;
    private byte minor;
    private int id;
    private boolean tls13Semantics;
    
    public VersionEntry(byte major, byte minor, int id, boolean tls13Semantics) {
        this.major = major;
        this.minor = minor;
        this.id = id;
        this.tls13Semantics = tls13Semantics;
    }
    
    public byte getMajor() {
        return major;
    }
    
    public byte getMinor() {
        return minor;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isTls13Sem() {
        return tls13Semantics;
    }
}
