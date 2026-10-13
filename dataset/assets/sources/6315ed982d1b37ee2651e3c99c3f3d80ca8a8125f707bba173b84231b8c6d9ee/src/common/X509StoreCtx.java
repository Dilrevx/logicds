package common;

import java.util.ArrayList;
import java.util.List;

public class X509StoreCtx {
    public List<X509> chain = new ArrayList<>();
    public X509VerifyParam param = new X509VerifyParam();
    public int error = 0;
    public X509StoreCtx parent = null;

    public static final int X509_V_OK                           = 0;
    public static final int X509_V_ERR_INVALID_CA                = 1;
    public static final int X509_V_ERR_INVALID_NON_CA            = 2;
    public static final int X509_V_ERR_EC_KEY_EXPLICIT_PARAMS    = 3;
    public static final int X509_V_ERR_UNSPECIFIED               = 4;
    public static final int X509_V_ERR_INVALID_EXTENSION         = 5;
    public static final int X509_V_ERR_PATH_LENGTH_EXCEEDED      = 6;
    public static final int X509_V_ERR_PROXY_PATH_LENGTH_EXCEEDED  = 7;

    public boolean verify_cb_cert(X509 x, int index, int err) {
        return false;
    }
}
