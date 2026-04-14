package commons;

public class Allocator {
    public static int allocate(Object mctx, int size, BufferHolder out) {
        out.buf = new Buffer();
        return Commons.ISC_R_SUCCESS;
    }

    public static class BufferHolder {
        public Buffer buf;
    }
}
