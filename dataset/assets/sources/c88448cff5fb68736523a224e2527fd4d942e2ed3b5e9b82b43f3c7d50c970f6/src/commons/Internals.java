package commons;

public class Internals {
    private boolean initialNegotiationCompleted;
    private int resumptionRequested;
    private int premasterSet;
    private SecurityParameters resumedSecurityParameters;
    private byte[] defaultHelloVersion = new byte[2];
    private Priorities priorities;
    private int hskFlags;
    private Dtls dtls;
    private int flags;
    
    public boolean isInitialNegotiationCompleted() {
        return initialNegotiationCompleted;
    }
    
    public int getResumptionRequested() {
        return resumptionRequested;
    }
    
    public int getPremasterSet() {
        return premasterSet;
    }

    public SecurityParameters getResumedSecurityParameters() {
        return resumedSecurityParameters;
    }
    
    public byte[] getDefaultHelloVersion() {
        return defaultHelloVersion;
    }
    
    public Priorities getPriorities() {
        return priorities;
    }
    
    public int getHskFlags() {
        return hskFlags;
    }
    
    public Dtls getDtls() {
        return dtls;
    }
    
    public int getFlags() {
        return flags;
    }

    public void setResumedSecurityParameters(SecurityParameters resumedParams) {
        this.resumedSecurityParameters = resumedParams;
    }

    public void setHskFlags(int i) {
        this.hskFlags = i;
    }

    public void setPriorities(Priorities priorities2) {
        this.priorities = priorities2;
    }

    public void setDefaultHelloVersion(byte[] bs) {
        this.defaultHelloVersion = bs;
    }

    public void setDtls(Dtls dtls) {
        this.dtls = dtls;
    }
}
