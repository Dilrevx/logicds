package commons;

public class X509Certificate {
    private X509Name subject;
    private X509Name issuer;
    private SerialNumber serialNumber;
    public X509Certificate(X509Name subj, X509Name issr, SerialNumber serialNumber) {
        this.subject = subj;
        this.issuer = issr;
        this.serialNumber = serialNumber;
    }

    public X509Name getSubjectName() { return subject; }
    public X509Name getIssuerName()  { return issuer; }
    public PubKey   getPublicKey()    { return new PubKey(); }
    public SerialNumber getSerialNumber() { return serialNumber; }
}
