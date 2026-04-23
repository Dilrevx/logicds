package common;

public class X509Util {
    public static int checkCurve(X509 x) {
        if (x.explicitCurveParams)
            return 0;
        return 1;
    }

      public static boolean checkPurpose(X509StoreCtx ctx, X509 x, int purpose, int idx, int mustBeCa) {
        return true;
    }
}
