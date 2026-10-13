package commons;

public class RC4_HMAC_MD5_HW {
    public int tls_init(CIPHER_BASE_CTX base, byte[] data, int len) {
        return (data != null && len > 0) ? 16 : 0;
    }

    public void init_mackey(CIPHER_BASE_CTX base, byte[] data, int len) {
    }
}
