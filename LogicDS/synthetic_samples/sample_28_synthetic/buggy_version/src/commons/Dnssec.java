package commons;

public class Dnssec {
    public static boolean selfSigns = true;
    public static boolean signs    = false;
    public static boolean dns_dnssec_selfsigns(RData r, Object o, Object ks, Object sigs, boolean b, Object m) {
        return true;
    }
    public static boolean dns_dnssec_signs(RData r, Object o, Object so, Object s, boolean b, Object m) {
        return signs;
    }
    public static int dns_dnssec_keyfromrdata(Object o, RData r, Object m, DstKey[] out) {
        out[0] = r.key;
        return Commons.ISC_R_SUCCESS;
    }
}
