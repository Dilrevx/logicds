public class ServerSettings {

    public String dnsKey = "";
    public String apiHost = "localhost";
    public int apiPort = 8081;

    public String getDnsKey() { return dnsKey; }
    public void setDnsKey(String dnsKey) { this.dnsKey = dnsKey; }

    public String getApiHost() { return apiHost; }
    public void setApiHost(String apiHost) { this.apiHost = apiHost; }

    public int getApiPort() { return apiPort; }
    public void setApiPort(int apiPort) { this.apiPort = apiPort; }
}
