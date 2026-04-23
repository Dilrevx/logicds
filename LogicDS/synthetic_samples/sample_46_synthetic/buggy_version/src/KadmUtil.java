public class KadmUtil {

    public static final int GSS_S_COMPLETE = 0;
    public static final int LOG_ERR = 3;

    public static int gssInquireContext(SvcReq rqstp, int[] minStat, Object ctx,
                                        Object srcNameOut, String[] targNameOut,
                                        Object lifetimeOut, Object mechTypeOut,
                                        Object ctxFlagsOut, Object localOut, Object openOut) {
        if (rqstp == null) {
            return -1;
        }
        minStat[0] = 0;
        targNameOut[0] = rqstp.getPreloadedName();
        return rqstp.getPreloadedInquireStat();
    }

    public static int gssToKrb5Name1(SvcReq rqstp, Object kctx, String name,
                                     KrbPrincipal[] princOut, GssBufferDesc gssStrOut) {
        if (rqstp == null) {
            return 0;
        }
        princOut[0] = rqstp.getPreloadedPrincipal();
        GssBufferDesc src = rqstp.getPreloadedGssStr();
        if (src != null) {
            gssStrOut.setValue(src.getValue());
            gssStrOut.setLength(src.getLength());
        }
        return rqstp.getPreloadedToKrb5NameStat();
    }

    public static int krb5PrincSize(Object kctx, KrbPrincipal princ) {
        if (princ == null || princ.getComponents() == null) {
            return 0;
        }
        return princ.getComponents().length;
    }

    public static KrbData krb5PrincComponent(Object kctx, KrbPrincipal princ, int i) {
        if (princ == null || princ.getComponents() == null) {
            return null;
        }
        return princ.getComponents()[i];
    }

    public static KrbData krb5PrincRealm(Object kctx, KrbPrincipal princ) {
        if (princ == null) {
            return null;
        }
        return princ.getRealm();
    }

    public static void truncName(int[] slen, String[] sdots) {
    }

    public static int strncmp(String s, byte[] data, int len) {
        if (s == null) {
            s = "";
        }
        if (data == null) {
            return -1;
        }
        int sLen = s.length();
        int dLen = data.length;
        int cmpLimit = Math.min(len, Math.min(sLen, dLen));
        for (int i = 0; i < cmpLimit; i++) {
            int a = s.charAt(i) & 0xff;
            int b = data[i] & 0xff;
            if (a != b) {
                return a - b;
            }
        }
        if (cmpLimit < len) {
            if (cmpLimit == sLen && cmpLimit < dLen) {
                return -(data[cmpLimit] & 0xff);
            }
            if (cmpLimit == dLen && cmpLimit < sLen) {
                return s.charAt(cmpLimit) & 0xff;
            }
        }
        return 0;
    }

    public static boolean dataEqString(KrbData kd, String s) {
        if (kd == null || s == null) {
            return false;
        }
        if (kd.getLength() != s.length()) {
            return false;
        }
        byte[] bytes = kd.getData();
        if (bytes == null) {
            return s.length() == 0;
        }
        for (int i = 0; i < kd.getLength(); i++) {
            int a = bytes[i] & 0xff;
            int b = s.charAt(i) & 0xff;
            if (a != b) {
                return false;
            }
        }
        return true;
    }

    public static void krb5KlogSyslog(int level, String fmt, Object... args) {
    }

    public static void logBadauth(int majStat, int minStat, Object xprt, Object name) {
    }

    public static void gssReleaseBuffer(int[] minStat, GssBufferDesc buf) {
    }

    public static void gssReleaseName(int[] minStat, String[] name) {
    }

    public static void krbFreePrincipal(Object kctx, KrbPrincipal princ) {
    }

    private KadmUtil() {}
}
