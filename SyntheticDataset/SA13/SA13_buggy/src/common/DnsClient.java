package common;

public class DnsClient {
    public String clientIp;
    public String peeraddr; // Simulated peer address.
    public DnsView view;

    public DnsClient(String clientIp, String peeraddr, DnsView view) {
        this.clientIp = clientIp;
        this.peeraddr = peeraddr;
        this.view = view;
    }
}
