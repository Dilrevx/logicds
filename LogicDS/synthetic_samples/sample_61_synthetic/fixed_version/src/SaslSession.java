public class SaslSession {

    public String authzeid = "";
    public String pendingeid = "";
    public String authzid = "";
    public MechPtr mechptr = new MechPtr();

    public String getAuthzeid() { return authzeid; }
    public void setAuthzeid(String authzeid) { this.authzeid = authzeid == null ? "" : authzeid; }

    public String getPendingeid() { return pendingeid; }
    public void setPendingeid(String pendingeid) { this.pendingeid = pendingeid == null ? "" : pendingeid; }

    public String getAuthzid() { return authzid; }
    public void setAuthzid(String authzid) { this.authzid = authzid == null ? "" : authzid; }

    public MechPtr getMechptr() { return mechptr; }
    public void setMechptr(MechPtr mechptr) { this.mechptr = mechptr; }
}
