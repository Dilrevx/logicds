package commons;

public class X509StoreCtx {
    private X509Certificate certificate;

    public X509StoreCtx(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public X509StoreCtx() {
        certificate = null;
    }

    private final X509Certificate cert =
            new X509Certificate(new X509Name("subj"),
                    new X509Name("issr"),
                    new SerialNumber("123"));
    public static X509StoreCtx newInstance() { return new X509StoreCtx(); }
    public void cleanup() { }
    public void free() { }

    public X509Certificate getCurrentCert() {
        return cert;
    }

    public int init(Object store, Object a, Object b) {
        return 1;
    }

    public CrlList get1_crls(X509Name name) {
        if ("issr".equals(name.toString())) {
            CrlList list = new CrlList();
            list.add(new Crl());
            return list;
        }
        return new CrlList();
    }
    public void setError(int err) { }
}