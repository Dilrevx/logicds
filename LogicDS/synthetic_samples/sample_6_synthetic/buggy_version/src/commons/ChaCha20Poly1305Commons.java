package commons;

public class ChaCha20Poly1305Commons {
    public static final int EVP_CTRL_INIT            = 0;
    public static final int EVP_CTRL_COPY            = 1;
    public static final int EVP_CTRL_AEAD_SET_IVLEN  = 2;
    public static final int EVP_CTRL_AEAD_SET_IV_FIXED = 3;
    public static final int EVP_CTRL_AEAD_SET_TAG    = 4;
    public static final int EVP_CTRL_AEAD_GET_TAG    = 5;
    public static final int EVP_CTRL_AEAD_TLS1_AAD   = 6;
    public static final int EVP_CTRL_AEAD_SET_MAC_KEY = 7;
    public static final int CHACHA_CTR_SIZE          = 16;
    public static final int POLY1305_BLOCK_SIZE      = 16;
    public static final int EVP_AEAD_TLS1_AAD_LEN    = 13;
    public static final int NO_TLS_PAYLOAD_LENGTH    = -1;
    public static final int CHACHA20_POLY1305_MAX_IVLEN = 12;

    public static EvpChaChaAeadCtx openSslZalloc() {
        return new EvpChaChaAeadCtx();
    }

    public static EvpChaChaAeadCtx openSslMemdup(EvpChaChaAeadCtx src) {
        return src.clone();
    }

    public static int CHACHA_U8TOU32(byte[] b, int off) {
        return (b[off] & 0xff)
                | ((b[off+1] & 0xff) << 8)
                | ((b[off+2] & 0xff) << 16)
                | ((b[off+3] & 0xff) << 24);
    }

    public static class EvpCipherCtx {
        public EvpChaChaAeadCtx aeadCtx;
        public boolean encrypt;
    }

    public static class EvpChaChaAeadCtx implements Cloneable {
        public Len len;
        public int aad;
        public int mac_inited;
        public int tag_len;
        public int nonce_len;
        public int tls_payload_length;
        public int[] nonce = new int[3];
        public Key key = new Key();
        public byte[] tag = new byte[POLY1305_BLOCK_SIZE];
        public EvpChaChaAeadCtx clone() {
            try { return (EvpChaChaAeadCtx)super.clone(); } catch (Exception e) { return null; }
        }
    }

    public static class Len {
        public int aad;
        public int text;
    }

    public static class Key {
        public int[] counter = new int[4];
    }
}
