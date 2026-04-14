import java.util.ArrayList;
import java.util.List;

public class X509StoreCtx {
    public List<X509Certificate> chain;
    public X509Params param;
    X509Certificate currentCert;
    boolean parent;
    public boolean bareTaSigned;
    public int error;

    public X509StoreCtx() {
        this.chain = new ArrayList<>();
        this.param = new X509Params();
        this.bareTaSigned = false;
    }

    boolean verifyCallback(int errorCode, X509StoreCtx ctx) {
        return errorCode != X509Error.X509_V_ERR_INVALID_POLICY_EXTENSION;
    }
}
