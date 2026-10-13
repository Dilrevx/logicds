package commons;

public class Commons {
    public static final Object tls_crl_store      = new Object();
    public static final int    OPENSSL_VERSION_NUMBER = 0x10100000;
    public static final boolean HAVE_LIBRESSL    = false;

    public static final int    OPENSSL_1_0          = 0x10000000;
    public static final int    OPENSSL_1_1          = 0x10100000;

    public static final int X509_V_ERR_CRL_SIGNATURE_FAILURE          = 100;
    public static final int X509_V_ERR_ERROR_IN_CRL_NEXT_UPDATE_FIELD = 101;
    public static final int X509_V_ERR_CRL_HAS_EXPIRED                = 102;
    public static final int X509_V_ERR_CERT_REVOKED                   = 103;

    public static final int FALSE = 0;

    public static void tls_log(String msg) { }
    public static void pr_trace_msg(String fmt, Object... args) { }

    public static CrlList get1_crls(X509StoreCtx ctx, X509Name name) {
        return ctx.get1_crls(name);
    }
}