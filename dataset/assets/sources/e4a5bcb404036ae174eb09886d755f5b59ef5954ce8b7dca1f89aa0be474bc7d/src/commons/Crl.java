package commons;

public class Crl {
    public int verify(PubKey key) { return 1; }
    public int nextUpdateCmp() { return 1; }
    public int revokedCount() { return 1; }
    public X509Revoked getRevoked(int i) { return new X509Revoked(new SerialNumber("123")); }
}