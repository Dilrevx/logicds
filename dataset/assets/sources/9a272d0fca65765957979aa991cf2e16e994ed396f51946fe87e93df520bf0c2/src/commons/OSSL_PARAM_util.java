package commons;

public class OSSL_PARAM_util {
    public static OSSL_PARAM OSSL_PARAM_locate_const(OSSL_PARAM[] params, String key) {
        for (OSSL_PARAM p : params) {
            if (p.name.equals(key)) return p;
        }
        return null;
    }
}
