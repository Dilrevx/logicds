package commons;

public class CtxtBase {
    public CipheringAlgorithmIdEnum cipher_algo;
    public IntegrityAlgorithmIdEnum integ_algo;
    public int tx_count;
    public int rx_count;
    public byte[] k_nas_enc = new byte[32];
    public byte[] k_nas_int = new byte[32];
}
