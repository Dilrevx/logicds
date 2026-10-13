package commons;

public class SecurityParameters {
    private long timestamp;
    private byte[] clientRandom;
    private byte[] sessionId;
    private int sessionIdSize;
    private int entity;
    private VersionEntry protocolVersion;
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public byte[] getClientRandom() {
        return clientRandom;
    }
    
    public byte[] getSessionId() {
        return sessionId;
    }
    
    public int getSessionIdSize() {
        return 100;
    }

    public int getEntity() {
        return entity;
    }
    
    public VersionEntry getProtocolVersion() {
        return protocolVersion;
    }
    
    public void setProtocolVersion(VersionEntry version) {
        this.protocolVersion = version;
    }

    public void setClientRandom() {
        this.clientRandom = new byte[16];
    }
}
