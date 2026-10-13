package commons;

public interface VerifyCallback {
    int call(int ok, X509_STORE_CTX ctx);
}