package commons;

public class Helper {
    public static EVP_PKEY X509_get_pubkey(X509 peer) {
        EVP_PKEY key = new EVP_PKEY();
        key.type = peer.cert_type;
        return key;
    }
    public static int X509_certificate_type(X509 peer, EVP_PKEY pkey) {
        return peer.cert_type;
    }
    public static void EVP_MD_CTX_init(EVP_MD_CTX ctx) {}
    public static void SSLerr(int f, int r) {}
    public static void ssl3_send_alert(SSL s, int level, int desc) {}
    public static boolean SSL_USE_SIGALGS(SSL s) { return false; }
    public static int tls12_check_peer_sigalg(EVP_MD_CTX[] mdHolder, SSL s, byte[] p, EVP_PKEY pkey) {
        return 1;
    }
    public static void n2s(byte[] p, int[] iHolder) { iHolder[0] = (p[0] & 0xFF)<<8 | (p[1]&0xFF); }
    public static int EVP_PKEY_size(EVP_PKEY pkey) { return 256; }
    public static boolean EVP_VerifyInit_ex(EVP_MD_CTX ctx, Object md, Object prov) { return true; }
    public static boolean EVP_VerifyUpdate(EVP_MD_CTX ctx, Object hdata, int len) { return true; }
    public static int EVP_VerifyFinal(EVP_MD_CTX ctx, byte[] sig, int sigLen, EVP_PKEY pkey) { return 1; }
    public static int RSA_verify(int nid, byte[] md, int mdLen, byte[] sig, int sigLen, Object rsa) { return 1; }
    public static int DSA_verify(int type, byte[] md, int mdLen, byte[] sig, int sigLen, Object dsa) { return 1; }
    public static int ECDSA_verify(int type, byte[] md, int mdLen, byte[] sig, int sigLen, Object ec) { return 1; }
    public static int EVP_PKEY_verify(Object ctx, byte[] sig, int sigLen, byte[] md, int mdLen) { return 1; }
}

