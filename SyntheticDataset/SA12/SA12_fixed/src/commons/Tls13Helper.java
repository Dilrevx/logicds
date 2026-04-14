package commons;

public class Tls13Helper {
    public static void WOLFSSL_START(int f) {}
    public static void WOLFSSL_ENTER(String s) {}
    public static void WOLFSSL_LEAVE(String s, int r) {}
    public static void WOLFSSL_END(int f) {}

    public static int DeriveFinishedSecret(WOLFSSL ssl, byte[] in, byte[] out) { return 0; }
    public static int BuildTls13HandshakeHmac(WOLFSSL ssl, byte[] secret, byte[] mac, int[] finishedSzHolder) {
        finishedSzHolder[0] = mac.length;
        return 0;
    }
    public static int XMEMCMP(byte[] a, int off, byte[] b, int len) {
        for (int i = 0; i < len; i++) {
            if (a[off + i] != b[i]) return 1;
        }
        return 0;
    }
    public static void SendAlert(WOLFSSL ssl, int level, int desc) {}
    public static int SetKeysSide(WOLFSSL ssl, int side) { return 0; }
    public static void DoCertFatalAlert(WOLFSSL ssl, int ret) {}
}
