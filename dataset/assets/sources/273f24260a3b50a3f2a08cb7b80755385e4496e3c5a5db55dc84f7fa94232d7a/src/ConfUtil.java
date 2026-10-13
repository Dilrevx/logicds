public class ConfUtil {

    public static void logPrintf(Object dest, int level, String fmt, Object... args) {
    }

    public static void printUsage() {
    }

    public static int configRead(MosquittoDb db, MosquittoConfig config, boolean reload) {
        return 0;
    }

    public static int configCheck(MosquittoConfig config) {
        return MosqErr.SUCCESS;
    }

    private ConfUtil() {}
}
