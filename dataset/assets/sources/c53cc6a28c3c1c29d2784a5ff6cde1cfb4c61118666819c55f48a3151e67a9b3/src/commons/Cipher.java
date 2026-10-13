package commons;

public class Cipher {
    private String name;
    private boolean hasProvider;
    private int referenceCount;

    public Cipher(String name) {
        this.name = name;
        this.hasProvider = true;
        this.referenceCount = 1;
    }

    public String getName() {
        return name;
    }

    public boolean isLegacy() {
        return name.equals("DES") || name.equals("RC4");
    }

    public boolean hasProvider() {
        return hasProvider;
    }

    public boolean incrementRefCount() {
        referenceCount++;
        return true;
    }

    public Object createNewContext() {
        return new Object();
    }

    public boolean encryptInit(Object algCtx, byte[] key, int keyLen, byte[] iv, int ivLen, CipherParams params) {
        return true;
    }

    public boolean decryptInit(Object algCtx, byte[] key, int keyLen, byte[] iv, int ivLen, CipherParams params) {
        return true;
    }

    public boolean hasEncryptInit() {
        return true;
    }

    public boolean hasDecryptInit() {
        return true;
    }

    public boolean cleanup(CipherContext ctx) {
        return true;
    }
}

