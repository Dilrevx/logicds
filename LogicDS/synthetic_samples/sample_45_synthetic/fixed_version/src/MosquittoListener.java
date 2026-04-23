public class MosquittoListener {

    private int port;
    private String host;
    private String mountPoint;
    private int maxConnections = -1;
    private MosqProtocol protocol = MosqProtocol.MQTT;
    private int socketDomain;
    private int clientCount;
    private Object socks;
    private int sockCount;
    private boolean useUsernameAsClientid;

    private int tlsVersion;
    private String cafile;
    private String capath;
    private String certfile;
    private String keyfile;
    private String ciphers;
    private String pskHint;
    private boolean requireCertificate;
    private Object sslCtx;
    private String crlfile;
    private boolean useIdentityAsUsername;
    private boolean useSubjectAsUsername;

    private SecurityOptions securityOptions = new SecurityOptions();

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getMountPoint() { return mountPoint; }
    public void setMountPoint(String mountPoint) { this.mountPoint = mountPoint; }
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
    public MosqProtocol getProtocol() { return protocol; }
    public void setProtocol(MosqProtocol protocol) { this.protocol = protocol; }
    public int getSocketDomain() { return socketDomain; }
    public void setSocketDomain(int socketDomain) { this.socketDomain = socketDomain; }
    public int getClientCount() { return clientCount; }
    public void setClientCount(int clientCount) { this.clientCount = clientCount; }
    public Object getSocks() { return socks; }
    public void setSocks(Object socks) { this.socks = socks; }
    public int getSockCount() { return sockCount; }
    public void setSockCount(int sockCount) { this.sockCount = sockCount; }
    public boolean isUseUsernameAsClientid() { return useUsernameAsClientid; }
    public void setUseUsernameAsClientid(boolean v) { this.useUsernameAsClientid = v; }
    public int getTlsVersion() { return tlsVersion; }
    public void setTlsVersion(int tlsVersion) { this.tlsVersion = tlsVersion; }
    public String getCafile() { return cafile; }
    public void setCafile(String cafile) { this.cafile = cafile; }
    public String getCapath() { return capath; }
    public void setCapath(String capath) { this.capath = capath; }
    public String getCertfile() { return certfile; }
    public void setCertfile(String certfile) { this.certfile = certfile; }
    public String getKeyfile() { return keyfile; }
    public void setKeyfile(String keyfile) { this.keyfile = keyfile; }
    public String getCiphers() { return ciphers; }
    public void setCiphers(String ciphers) { this.ciphers = ciphers; }
    public String getPskHint() { return pskHint; }
    public void setPskHint(String pskHint) { this.pskHint = pskHint; }
    public boolean isRequireCertificate() { return requireCertificate; }
    public void setRequireCertificate(boolean v) { this.requireCertificate = v; }
    public Object getSslCtx() { return sslCtx; }
    public void setSslCtx(Object sslCtx) { this.sslCtx = sslCtx; }
    public String getCrlfile() { return crlfile; }
    public void setCrlfile(String crlfile) { this.crlfile = crlfile; }
    public boolean isUseIdentityAsUsername() { return useIdentityAsUsername; }
    public void setUseIdentityAsUsername(boolean v) { this.useIdentityAsUsername = v; }
    public boolean isUseSubjectAsUsername() { return useSubjectAsUsername; }
    public void setUseSubjectAsUsername(boolean v) { this.useSubjectAsUsername = v; }
    public SecurityOptions getSecurityOptions() { return securityOptions; }
    public void setSecurityOptions(SecurityOptions securityOptions) { this.securityOptions = securityOptions; }
}
