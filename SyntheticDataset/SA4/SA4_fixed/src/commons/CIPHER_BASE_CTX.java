package commons;

public class CIPHER_BASE_CTX {
    public int keylen = 16;
    public int ivlen = 16;
    public int tlsversion;

    public byte[] lastMacKeyUsed;
}
