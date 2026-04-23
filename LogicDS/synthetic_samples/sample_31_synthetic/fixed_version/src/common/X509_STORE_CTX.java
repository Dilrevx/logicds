package common;
import java.util.List;

public class X509_STORE_CTX {
    public void set_verify_cb(VerifyCallback cb) {}
    public static int verify_cert(X509_STORE_CTX ctx) { return -1; }
    public int get_error() { return -1; }
    public List<X509> get0_chain() { return null; }
    public List<X509> get1_chain() { return null; }
    public void free() {}
}