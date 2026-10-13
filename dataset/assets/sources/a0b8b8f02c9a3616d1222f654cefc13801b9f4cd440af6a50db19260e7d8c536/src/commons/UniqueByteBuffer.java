package commons;

public class UniqueByteBuffer {
    public byte[] msg;
    public int N_bytes;
    public UniqueByteBuffer(byte[] data) {
        this.msg = data;
        this.N_bytes = data.length;
    }
    public byte[] get() {
        return msg;
    }
}