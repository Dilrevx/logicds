public class KrbPaOtpReq {

    private byte[] encData;
    private byte[] nonce;

    public byte[] getEncData() { return encData; }
    public void setEncData(byte[] encData) { this.encData = encData; }

    public byte[] getNonce() { return nonce; }
    public void setNonce(byte[] nonce) { this.nonce = nonce; }
}
