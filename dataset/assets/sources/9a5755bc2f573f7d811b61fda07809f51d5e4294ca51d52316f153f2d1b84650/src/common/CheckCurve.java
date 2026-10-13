package common;

public class CheckCurve {
    public static int checkCurve(X509 x) {
        if (x.explicitCurveParams) {
            return 0;
        }
        return 1;
    }
}
