package commons;

public class Buffer {
    private byte[] data;
    private int length;
    
    public Buffer() {
    }
    
    public int initHandshakeMbuffer() {
        return 0;
    }
    
    public int appendData(byte[] src, int offset, int length) {
        return 0;
    }
    
    public int appendDataPrefix(int prefixBits, byte[] src, int offset, int length) {
        return 0;
    }
    
    public void clear() {
    }
    
    public Buffer toMbuffer() {
        return this;
    }
}
