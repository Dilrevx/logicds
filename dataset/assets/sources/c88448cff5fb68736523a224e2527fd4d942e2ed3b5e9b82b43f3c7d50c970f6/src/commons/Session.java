package commons;

public class Session {
    private Internals internals;
    private SecurityParameters securityParameters;
    
    public Internals getInternals() {
        return internals;
    }
    
    public SecurityParameters getSecurityParameters() {
        return securityParameters;
    }
    
    public boolean isDTLS() {
        return true;
    }
    
    public VersionEntry getVersion() {
        return null;
    }
    
    public void setAdvVersion(byte major, byte minor) {
    }
    
    public boolean setCurrentVersion(int id) {
        return true;
    }
    
    public void setDefaultVersion(VersionEntry ver) {
    }
    
    public static final int INT_FLAG_NO_TLS13 = 0x1000;

    public void setInternals(Internals internals) {
        this.internals = internals;
    }

    public void setSecurityParameters(SecurityParameters secParams) {
        this.securityParameters = secParams;
    }
}
