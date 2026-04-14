package commons;

import java.util.List;

public class X509_STORE_CTX {
    public X509 cert;
    public List<X509> chain;
    public List<X509> untrusted;
    public int last_untrusted;
    public X509_VERIFY_PARAM param;
    public VerifyCallback verify_cb;
    public int error;
    public X509 current_cert;
    public int error_depth;
    public static X509 lastIssuer;
    public int get_issuer(X509 cert) { lastIssuer = cert; return 0; }
    public boolean check_revocation(X509_STORE_CTX ctx) { return true; }
    public boolean check_policy(X509_STORE_CTX ctx) { return true; }
    public boolean check_chain_extensions(X509_STORE_CTX ctx) { return true; }
    public boolean check_name_constraints(X509_STORE_CTX ctx) { return true; }
    public boolean check_id(X509_STORE_CTX ctx) { return true; }
}

