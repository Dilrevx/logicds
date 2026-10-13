public class AuthenticationBackendConfiguration {

    private boolean refreshProfile;
    private long refreshProfileIntervalMillis;

    public boolean isRefreshProfile() { return refreshProfile; }
    public void setRefreshProfile(boolean refreshProfile) { this.refreshProfile = refreshProfile; }

    public long getRefreshProfileIntervalMillis() { return refreshProfileIntervalMillis; }
    public void setRefreshProfileIntervalMillis(long v) { this.refreshProfileIntervalMillis = v; }
}
