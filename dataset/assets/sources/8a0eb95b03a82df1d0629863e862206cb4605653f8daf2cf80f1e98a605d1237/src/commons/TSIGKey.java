package commons;

public class TSIGKey {
    public DNSName name;

    public TSIGKey(DNSName dnsName) {
        this.name = dnsName;
    }

    public DNSName identity() { return this.name; }
}
