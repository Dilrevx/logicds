package commons;
public class MbedtlsX509Crt {
    public MbedtlsX509Buf raw     = new MbedtlsX509Buf();
    public boolean     ownBuffer;
    public MbedtlsX509Buf sigOid  = new MbedtlsX509Buf();
    public byte[]        sig;
}
