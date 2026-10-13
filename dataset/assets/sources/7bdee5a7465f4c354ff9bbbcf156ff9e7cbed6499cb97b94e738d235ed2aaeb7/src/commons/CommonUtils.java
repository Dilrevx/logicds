package commons;

public class CommonUtils {
    public static int MBEDTLS_OID_CMP(Oid a, Oid b) {
        if (a.oid.length != b.oid.length) return -1;
        for (int i = 0; i < a.oid.length; i++) {
            if (a.oid[i] != b.oid[i]) return -1;
        }
        return 0;
    }
    public static int x509_crt_check_cn(Buf name, String cn, int cnLen) {
        String s = new String(name.data, 0, name.length);
        return s.equals(cn) ? 0 : -1;
    }

    public static int x509_crt_check_san(Buf name, String cn, int cnLen) {
        int sanType = name.data[0] & 0x1F;
        if (sanType == MbedtlsConstants.MBEDTLS_X509_SAN_DNS_NAME) {
            return x509_crt_check_cn(name, cn, cnLen);
        }
        return -1;
    }

}
