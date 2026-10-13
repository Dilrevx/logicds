package commons;

public class MemUtil {
    public static int memcmp(byte[] a, byte[] b, int len) {
        for (int i = 0; i < len; i++) {
            int va = a[i] & 0xFF;
            int vb = b[i] & 0xFF;
            if (va != vb) return va < vb ? -1 : 1;
        }
        return 0;
    }
}
