package commons;

public class MbedtlsX509CrlEntry {
    public X509Buf serial = new X509Buf();
    public MbedtlsX509Time revocationDate;
    public MbedtlsX509CrlEntry next;
}
