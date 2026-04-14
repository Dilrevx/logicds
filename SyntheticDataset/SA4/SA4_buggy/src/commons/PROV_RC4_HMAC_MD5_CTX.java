package commons;

public class PROV_RC4_HMAC_MD5_CTX {
    public CIPHER_BASE_CTX base;
    public int tls_aad_pad_sz;
    public RC4_HMAC_MD5_HW hw;

    public PROV_RC4_HMAC_MD5_CTX() {
        this.base = new CIPHER_BASE_CTX();
        this.hw = new RC4_HMAC_MD5_HW();
    }
}
