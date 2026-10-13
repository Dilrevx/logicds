package commons;

import java.util.List;

public class Helper {
    public static X509 lastIssuer;
    public static int checkTrustReturn = Constants.X509_TRUST_TRUSTED;
    public static void raiseError(String code) {}
    public static boolean cert_self_signed(X509 x) { return false; }
    public static X509 find_issuer(X509_STORE_CTX ctx, List<X509> list, X509 x) { return null; }
    public static boolean equals(X509 a, X509 b) { return a == b; }
    public static void free(X509 x) {}
    public static boolean booleanOk(int ok) { return ok > 0; }
    public static int check_trust(X509_STORE_CTX ctx) {
        return checkTrustReturn;
    }
}