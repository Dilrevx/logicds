public class EvpCipher {
    public enum Origin { METHOD, PROVIDER }

    public static final int NID_UNDEF = -1;
    public static final int CTX_NO_PADDING = 0x1;
    public static final int CTX_FLAG_WRAP_ALLOW = 0x2;
    public static final int CTRL_INIT = 0x4;
    public static final int CTX_CUSTOM_IV = 0x10;

    public static final int MODE_STREAM_CIPHER = 1;
    public static final int MODE_ECB = 2;
    public static final int MODE_CFB = 3;
    public static final int MODE_OFB = 4;
    public static final int MODE_CBC = 5;
    public static final int MODE_CTR = 6;

    private int nid;
    private Origin origin;
    private Object prov;
    private int ctxSize;
    private int keyLen;
    private int flags;
    private int blockSize;
    private CipherInit encryptInit;
    private CipherInit decryptInit;

    public interface CipherInit {
        int init(Object algCtx, byte[] key, int keyLen, byte[] iv, int ivLen, OsslParam[] params);
    }

    public EvpCipher() {
        this.nid = NID_UNDEF;
        this.origin = Origin.PROVIDER;
        this.prov = null;
        this.ctxSize = 0;
        this.keyLen = 16;
        this.flags = 0;
        this.blockSize = 16;
        this.encryptInit = null;
        this.decryptInit = null;
    }

    public int getNid() {
        return nid;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Object getProv() {
        return prov;
    }

    public int getCtxSize() {
        return ctxSize;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public int getFlags() {
        return flags;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public CipherInit getEncryptInit() {
        return encryptInit;
    }

    public CipherInit getDecryptInit() {
        return decryptInit;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public void setProv(Object prov) {
        this.prov = prov;
    }

    public void setCtxSize(int ctxSize) {
        this.ctxSize = ctxSize;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void setEncryptInit(CipherInit encryptInit) {
        this.encryptInit = encryptInit;
    }

    public void setDecryptInit(CipherInit decryptInit) {
        this.decryptInit = decryptInit;
    }


    public static EvpCipher fetch(Object unused, String name, String options) {
        if ("NULL".equals(name)) {
            return null;
        }
        EvpCipher cipher = new EvpCipher();
        cipher.nid = NID_UNDEF;
        cipher.blockSize = 16;
        cipher.keyLen = 16;
        return cipher;
    }

    public static void free(EvpCipher cipher) {
        cipher = null;
    }

    public static boolean upRef(EvpCipher cipher) {
        return cipher != null;
    }

    public Object newCtx(Object providerCtx) {
        return new Object();
    }
}
