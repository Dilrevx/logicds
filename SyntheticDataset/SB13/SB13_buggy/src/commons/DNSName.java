package commons;

public class DNSName {
    public Buffer buffer;

    public static boolean hasBuffer(DNSName n) {
        return n.buffer != null;
    }

    public static void setBuffer(DNSName n, Buffer b) {
        n.buffer = b;
    }

    public static void clone(DNSName src, DNSName dst) { }
}
