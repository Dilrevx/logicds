public class X509Certificate {
    private int exFlags;

    public X509Certificate(int flags) {
        this.exFlags = flags;
    }

    public int hashCode() {
        return exFlags;
    }
}
