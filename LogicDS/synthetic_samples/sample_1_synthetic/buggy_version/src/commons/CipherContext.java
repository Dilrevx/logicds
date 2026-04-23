package commons;

import java.util.HashMap;
import java.util.Map;

public class CipherContext {
    private Cipher cipher;
    private boolean encrypt;
    private Engine engine;
    private Object algCtx;
    private Cipher fetchedCipher;
    private long flags;
    private Map<String, Integer> params;
    private int keyLength = 16;
    private int ivLength = 16;

    public CipherContext(Cipher cipher, boolean encrypt, Engine engine) {
        this.cipher = cipher;
        this.encrypt = encrypt;
        this.engine = engine;
        this.params = new HashMap<>();
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setEncrypt(int enc) {
        this.encrypt = (enc == 1);
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public Cipher getFetchedCipher() {
        return fetchedCipher;
    }

    public void setFetchedCipher(Cipher cipher) {
        this.fetchedCipher = cipher;
    }

    public void freeFetchedCipher() {
        this.fetchedCipher = null;
    }

    public boolean cleanup(CipherContext ctx) {
        return true;
    }

    public void clearCipherData() {
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public void reset() {
        this.cipher = null;
        this.encrypt = false;
        this.engine = null;
        this.flags = 0;
        this.algCtx = null;
        this.params.clear();
    }

    public Object getAlgCtx() {
        return algCtx;
    }

    public void setAlgCtx(Object algCtx) {
        this.algCtx = algCtx;
    }

    public boolean hasNoPadding() {
        return (flags & 1) != 0;
    }

    public boolean setPadding(int padding) {
        return true;
    }

    public boolean setParams(CipherParams paramLens) {
        this.params.putAll(paramLens.getAllParams());
        return true;
    }

    public int getParam(String key) {
        if (this.params == null) {
            return -1;
        }
        if (key == null || !this.params.containsKey(key)) {
            return -1;
        }
        return this.params.get(key);
    }

    public int getKeyLength() {
        return keyLength;
    }

    public int getIvLength() {
        return ivLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public void setIvLength(int ivLength) {
        this.ivLength = ivLength;
    }
}


