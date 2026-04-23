package commons;

public class MbedtlsConstants {
    public static final int MBEDTLS_X509_EXT_SUBJECT_ALT_NAME    = 1;
    public static final int MBEDTLS_X509_BADCERT_CN_MISMATCH     = 1 << 1;
    public static final int MBEDTLS_X509_SAN_DNS_NAME            = 2;
    public static final Oid MBEDTLS_OID_AT_CN                   = new Oid(new byte[] { 0x55, (byte)0x04, 0x03 });
}
