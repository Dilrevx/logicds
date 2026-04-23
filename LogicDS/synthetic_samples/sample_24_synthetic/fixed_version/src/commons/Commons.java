package commons;

public class Commons {
    public static final int BAD_FUNC_ARG         = -1;
    public static final int CERTREQ_TYPE         = 1;
    public static final int CERT_TYPE            = CERTREQ_TYPE;
    public static final int SIG_STATE_BEGIN      = 0;
    public static final boolean ALLOW_V1_EXTENSIONS = false;
    public static final int ASN_VERSION_E        = -2;
    public static final int ASN_BEFORE_DATE_E    = -3;
    public static final int ASN_AFTER_DATE_E     = -4;
    public static final int ASN_CRIT_EXT_E       = -5;
    public static final int ASN_SIG_OID_E        = -6;

    public static final int VERIFY_OCSP_CERT     = 10;
    public static final int VERIFY_OCSP          = 11;
    public static final int NO_VERIFY            = 0;
    public static final int VERIFY               = 4;
    public static final int VERIFY_SKIP_DATE     = 5;
    public static final int VERIFY_NAME          = 6;
    public static final int CA_TYPE              = 2;
    public static final int TRUSTED_PEER_TYPE    = 3;

    public static final int OCSP_ONLY            = 20;
    public static final int FULL_VERIFY          = 21;

    public static int DecodeToKey(DecodedCert cert, int verify) {
        return 0;
    }
    public static int DecodeCertExtensions(DecodedCert cert) {
        return 0;
    }
    public static int GetAlgoId(DecodedCert cert) {
        return 0x1234;
    }
    public static int GetSignature(DecodedCert cert) {
        return 0;
    }
}
