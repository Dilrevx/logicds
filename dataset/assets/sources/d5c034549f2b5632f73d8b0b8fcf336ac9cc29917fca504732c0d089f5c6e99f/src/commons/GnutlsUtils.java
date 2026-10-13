package commons;

public class GnutlsUtils {
    public static void gnutls_assert() {}
    public static void gnutls_free(GnutlsDatum d) {}
    public static int _gnutls_fbase64_decode(String header, byte[] data, int size, GnutlsDatum out) { return 1; }
    public static int _gnutls_set_datum(GnutlsDatum out, byte[] data, int size) { return 1; }
    public static int crt_reinit(GnutlsX509Crt cert) { return 1; }
    public static int asn1_der_decoding(Object asn1, byte[] data, int size, Object dummy) { return GnutlsConstants.ASN1_SUCCESS; }
    public static int _gnutls_asn2err(int code) { return code; }
    public static int _gnutls_x509_get_raw_field2(Object cert, GnutlsDatum der, String field, GnutlsDatum out) { return 1; }
    public static int gnutls_x509_crt_get_version(GnutlsX509Crt cert) { return 3; }
    public static void _gnutls_free_datum(GnutlsDatum d) {}
    public static void _gnutls_debug_log(String msg) {}
    public static int testSigAlgoOuter = -1;
    public static int testSigAlgoInner = -1;

    public static int _gnutls_x509_get_signature_algorithm(Object cert, String field) {
        if ("signatureAlgorithm.algorithm".equals(field) && testSigAlgoOuter != -1) {
            return testSigAlgoOuter;
        }
        if ("tbsCertificate.signature.algorithm".equals(field) && testSigAlgoInner != -1) {
            return testSigAlgoInner;
        }
        return 1;
    }

    public static String gnutls_sign_get_name(int signAlgo) {
        return "OID_" + signAlgo;
    }
}
