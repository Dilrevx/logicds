public class EvpCipherCtx {
    private EvpCipher cipher;
    private EvpCipher fetchedCipher;
    private Engine engine;
    private Object algCtx;
    private int encrypt;
    private int ivLen;
    private byte[] cipherData;
    private long flags;
    private int keyLen;
    private int num;
    private byte[] iv;
    private byte[] oiv;

    public EvpCipher getCipher() {
        return cipher;
    }

    public void setCipher(EvpCipher cipher) {
        this.cipher = cipher;
    }

    public EvpCipher getFetchedCipher() {
        return fetchedCipher;
    }

    public void setFetchedCipher(EvpCipher fetchedCipher) {
        this.fetchedCipher = fetchedCipher;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Object getAlgCtx() {
        return algCtx;
    }

    public void setAlgCtx(Object algCtx) {
        this.algCtx = algCtx;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public int getIvLen() {
        return ivLen;
    }

    public void setIvLen(int ivLen) {
        this.ivLen = ivLen;
    }

    public byte[] getCipherData() {
        return cipherData;
    }

    public void setCipherData(byte[] cipherData) {
        this.cipherData = cipherData;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getOiv() {
        return oiv;
    }

    public void setOiv(byte[] oiv) {
        this.oiv = oiv;
    }

    public static void reset(EvpCipherCtx ctx) {
        ctx.setCipher(null);
        ctx.setFetchedCipher(null);
        ctx.setEngine(null);
        ctx.setAlgCtx(null);
        ctx.setCipherData(null);
        ctx.setIv(null);
        ctx.setOiv(null);
        ctx.setNum(0);
    }

    public static boolean setPadding(EvpCipherCtx ctx, int padding) {
        return true;
    }

    public static int getKeyLength(EvpCipherCtx ctx) {
        return ctx.getKeyLen();
    }

    public static int getIvLength(EvpCipherCtx ctx) {
        return ctx.getIvLen();
    }

    public static boolean ctrl(EvpCipherCtx ctx, int command, int p1, Object p2) {
        return true;
    }

    public static int getMode(EvpCipherCtx ctx) {
        if (ctx.getCipher() != null) {
            return ctx.getCipher().getFlags();
        }
        return EvpCipher.MODE_CBC;
    }
}
