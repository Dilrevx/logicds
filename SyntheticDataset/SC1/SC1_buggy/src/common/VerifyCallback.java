package common;

public interface VerifyCallback {
    int verify(X509_STORE_CTX ctx, Object arg);
}