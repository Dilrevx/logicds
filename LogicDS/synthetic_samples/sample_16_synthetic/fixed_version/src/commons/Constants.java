package commons;

public class Constants {
    public static final int SSL3_ST_SR_CERT_VRFY_A = 100;
    public static final int SSL3_ST_SR_CERT_VRFY_B = 101;
    public static final int SSL3_RT_MAX_PLAIN_LENGTH = 16384;
    public static final int SSL3_MT_CERTIFICATE_VERIFY = 15;

    public static final int EVP_PKT_SIGN = 0x02;
    public static final int EVP_PKT_DH = 0x20;
    public static final int EVP_PKEY_RSA = 1;
    public static final int EVP_PKEY_DSA = 2;
    public static final int EVP_PKEY_EC = 3;
    public static final int NID_id_GostR3410_94 = 200;
    public static final int NID_id_GostR3410_2001 = 201;

    public static final int SSL_AD_UNEXPECTED_MESSAGE = 10;
    public static final int SSL_AD_ILLEGAL_PARAMETER = 11;
    public static final int SSL_AD_DECODE_ERROR = 12;
    public static final int SSL_AD_INTERNAL_ERROR = 13;
    public static final int SSL_AD_DECRYPT_ERROR = 14;
    public static final int SSL3_AL_FATAL = 40;

    public static final int SSL_R_MISSING_VERIFY_MESSAGE = 30;
    public static final int SSL_R_NO_CLIENT_CERT_RECEIVED = 31;
    public static final int SSL_R_SIGNATURE_FOR_NON_SIGNING_CERTIFICATE = 32;
    public static final int SSL_R_CCS_RECEIVED_EARLY = 33;
    public static final int SSL_R_LENGTH_MISMATCH = 34;
    public static final int SSL_R_WRONG_SIGNATURE_SIZE = 35;
    public static final int SSL_R_BAD_SIGNATURE = 36;
    public static final int SSL_R_BAD_RSA_SIGNATURE = 37;
    public static final int SSL_R_BAD_DSA_SIGNATURE = 38;
    public static final int SSL_R_BAD_ECDSA_SIGNATURE = 39;

    public static final int SSL_F_SSL3_GET_CERT_VERIFY = 50;
}
