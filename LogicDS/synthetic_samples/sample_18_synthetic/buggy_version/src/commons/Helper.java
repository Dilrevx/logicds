package commons;

public class Helper {
    public static final int MAX_HASH_SIZE = 64;
    public static byte[] testRserial;
    public static byte[] testCserial;
    public static byte[] testRdnHash;
    public static int testHashLen;
    public static int[] okHolder = new int[1];

    public static void setRserial(byte[] r) { testRserial = r; }
    public static void setCserial(byte[] c) { testCserial = c; }
    public static void setRdnHash(byte[] h, int len) { testRdnHash = h; testHashLen = len; }


    public static int gnutls_ocsp_resp_get_single(Object resp, int indx,
                                                  DigestAlgorithm[] digestOut, Datum rdn_hash, Object a,
                                                  Datum rserial, Object b, Object c, Object d, Object e, Object f) {
        rdn_hash.data = testRdnHash;
        rdn_hash.size = testHashLen;
        rserial.data = testRserial;
        rserial.size = testRserial.length;
        return 0;
    }

    public static int gnutls_x509_crt_get_serial(Object crt, Datum serial, long[] tHolder) {
        int needed = testCserial.length;
        if (serial.data == null || serial.data.length < needed) {
            serial.data = new byte[needed];
        }
        System.arraycopy(testCserial, 0, serial.data, 0, needed);
        tHolder[0] = needed;
        return 0;
    }

    public static int gnutls_x509_crt_get_raw_issuer_dn(Object crt, Datum dn) {
        dn.data = new byte[] { }; dn.size = 0;
        return 0;
    }

    public static int gnutls_hash_get_algo_len(DigestAlgorithm alg) {
        return testHashLen;
    }

    public static int gnutls_hash_fast(DigestAlgorithm alg, byte[] data, int size, byte[] out) {
        System.arraycopy(testRdnHash, 0, out, 0, testHashLen);
        return 0;
    }

    public static boolean memcmp(byte[] a, byte[] b, int len) {
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
}
