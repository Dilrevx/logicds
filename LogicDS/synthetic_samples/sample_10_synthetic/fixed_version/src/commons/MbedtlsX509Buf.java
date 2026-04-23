package commons;

public class MbedtlsX509Buf {
    public byte[] p;
    public int len;
    public int tag;

    private static int instanceCount = 0;

    public MbedtlsX509Buf() {
        synchronized (MbedtlsX509Buf.class) {
            instanceCount++;
            if (Helper.testMode == 2) {
                this.tag = instanceCount;
            } else {
                this.tag = 1;
            }
        }
    }
}
