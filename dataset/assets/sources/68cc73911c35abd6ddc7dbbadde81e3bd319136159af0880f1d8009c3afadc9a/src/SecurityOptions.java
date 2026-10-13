public class SecurityOptions {

    private String passwordFile;
    private String pskFile;
    private String aclFile;
    private Object authPluginConfigs;
    private int authPluginConfigCount;
    private int allowAnonymous = -1;

    public String getPasswordFile() { return passwordFile; }
    public void setPasswordFile(String passwordFile) { this.passwordFile = passwordFile; }

    public String getPskFile() { return pskFile; }
    public void setPskFile(String pskFile) { this.pskFile = pskFile; }

    public String getAclFile() { return aclFile; }
    public void setAclFile(String aclFile) { this.aclFile = aclFile; }

    public Object getAuthPluginConfigs() { return authPluginConfigs; }
    public void setAuthPluginConfigs(Object authPluginConfigs) { this.authPluginConfigs = authPluginConfigs; }

    public int getAuthPluginConfigCount() { return authPluginConfigCount; }
    public void setAuthPluginConfigCount(int v) { this.authPluginConfigCount = v; }

    public int getAllowAnonymous() { return allowAnonymous; }
    public void setAllowAnonymous(int v) { this.allowAnonymous = v; }
}
