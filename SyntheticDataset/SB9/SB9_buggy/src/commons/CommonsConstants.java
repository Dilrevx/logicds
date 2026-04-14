package commons;

public class CommonsConstants {
    public static final int TLS_ASYNC_BEGIN = 0;
    public static final int TLS_ASYNC_BUILD = 1;
    public static final int ENUM_LEN = 2;
    public static final int OPAQUE16_LEN = 2;
    public static final int ENCRYPT_LEN = 1024;
    public static final int BUFFER_ERROR = -2;
    public static final int SIG_VERIFY_E = -10;
    public static final int INVALID_PARAMETER = -3;
    public static final byte sha_mac = 0;
    public static final byte anonymous_sa_algo = 0;
    public static final byte ed25519_sa_algo = 1;
    public static final byte ed448_sa_algo = 2;
    public static final byte ecc_dsa_sa_algo = 3;
    public static final byte falcon_level1_sa_algo = 4;
    public static final byte falcon_level5_sa_algo = 5;
    public static final byte rsa_sa_algo = 6;
    public static final byte rsa_pss_sa_algo = 7;
}