package commons;

public class Commons {
    public static final int TLS_ASYNC_BEGIN       = 0;
    public static final int TLS_ASYNC_BUILD       = 1;
    public static final int TLS_ASYNC_DO          = 2;
    public static final int TLS_ASYNC_VERIFY      = 3;
    public static final int TLS_ASYNC_FINALIZE    = 4;

    public static final int BUFFER_ERROR          = -1;
    public static final int INVALID_PARAMETER     = -2;
    public static final int SIG_VERIFY_E          = -3;

    public static final int sha_mac               = 0;
    public static final int anonymous_sa_algo     = 0;
    public static final int ed25519_sa_algo       = 1;
    public static final int ed448_sa_algo         = 2;
    public static final int ecc_dsa_sa_algo       = 3;
    public static final int rsa_sa_algo           = 4;
    public static final int rsa_pss_sa_algo       = 5;

    public static final int ENUM_LEN              = 1;
    public static final int OPAQUE16_LEN          = 2;
    public static final int ENCRYPT_LEN           = 0xFFFF;

    public static final int SERVER_CERT_VERIFY_COMPLETE = 10;
}
