package commons;

public class X509StoreContext {
    private boolean verified = false;
    public static boolean simulateSuccess;
    public static boolean errorCaptured;

    public static X509StoreContext newContext() {
        return new X509StoreContext();
    }

    public boolean init(X509Store store, X509 signer, X509Stack untrusted) {
        return true;
    }

    public X509VerifyParam getParam() {
        return new X509VerifyParam();
    }

    public void setPurpose(int purpose) {}
    public void setTrust(int trust) {}

    public int verify() {
        if (simulateSuccess) {
            verified = true;
            return 1;
        }
        return 0;
    }

    public int getError() {
        errorCaptured = true;
        return 3;
    }

    public String getErrorString() {
        return "unable to get certificate CRL";
    }

    public X509Stack getVerifiedChain() {
        return new X509Stack();
    }

    public void free() {}
}
