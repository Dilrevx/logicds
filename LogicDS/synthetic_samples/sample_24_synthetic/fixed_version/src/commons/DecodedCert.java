package commons;

public class DecodedCert {
    public SigCtx sigCtx = new SigCtx();
    public int    srcIdx;
    public int    sigIndex;
    public int    version;
    public boolean isCSR;
    public boolean ocspNoCheckSet;
    public byte[]  source;
    public byte[]  extensions;
    public int     extensionsSz;
    public int     extensionsIdx;
    public int     badDate;
    public int     criticalExt;
    public int     signatureOID;
    public Object  ca;
}
