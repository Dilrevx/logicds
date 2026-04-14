package commons;

import main.X509Verifier;

import java.util.List;
public class X509Utils {
    public static boolean isTest2 = false;
    public static X509 x509_verify_chain_last(X509VerifyChain chain) {
        List<X509> c = chain.certs;
        return c.get(c.size() - 1);
    }
    public static X509 x509_verify_chain_leaf(X509VerifyChain chain) {
        return chain.certs.get(0);
    }
    public static int sk_X509_num(List<X509> certs) {
        return certs.size();
    }
    public static int x509_verify_cert_error(X509VerifyCtx ctx, X509 cert, int depth, int err, int zero) {
        return -1;
    }
    public static boolean x509_verify_ctx_validate_legacy_chain(X509VerifyCtx ctx, X509VerifyChain chain, int depth) {
        return true;
    }
    public static X509VerifyChain x509_verify_chain_dup(X509VerifyChain chain) {
        return chain;
    }
    public static boolean x509_verify_cert_valid(X509VerifyCtx ctx, X509 leaf, Object dummy) {
        return !isTest2;
    }
    public static boolean x509_verify_cert_hostname(X509VerifyCtx ctx, X509 leaf, String name) {
        return true;
    }
}
