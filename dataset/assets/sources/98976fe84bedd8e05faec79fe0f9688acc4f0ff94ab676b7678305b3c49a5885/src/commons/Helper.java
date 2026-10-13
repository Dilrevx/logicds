package commons;

public class Helper {
    public static int testMode = 0;
    public static int x509GetAlgCount;

    public static int asn1GetTag(int p, byte[] buf, int end, int tag) {
        return 0;
    }

    public static int x509GetAlg(byte[] buf, int p, MbedtlsX509Buf oid, MbedtlsX509Buf params) {
        oid.p = new byte[]{1,2,3};
        oid.len = 3;
        params.p = new byte[0];
        params.len = 1;
        if (testMode == 0) {
            params.tag = 0;
        } else {
            params.tag = (x509GetAlgCount++ == 0 ? 0x05 : 0x00);
        }
        return 0;
    }

    public static int x509GetSigAlg(MbedtlsX509Buf oid, MbedtlsX509Buf params,
                                    int[] md, int[] pk, int[] opts) {
        return 0;
    }

    public static int x509GetSig(byte[] buf, int p, byte[][] sigHolder) {
        sigHolder[0] = new byte[]{0};
        return 0;
    }
}
