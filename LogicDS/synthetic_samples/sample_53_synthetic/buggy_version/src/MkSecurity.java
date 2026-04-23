import java.util.ArrayList;
import java.util.List;

public class MkSecurity {

    public static final List<String> blockedPrefixes = new ArrayList<String>();
    public static boolean hotlinkBlocked = false;

    public static int checkUrl(MkPtr uri) {
        if (uri == null || uri.data == null) {
            return 0;
        }
        for (int i = 0; i < blockedPrefixes.size(); i++) {
            if (uri.data.startsWith(blockedPrefixes.get(i))) {
                return -1;
            }
        }
        return 0;
    }

    public static int checkHotlink(MkPtr uriProcessed, MkPtr host, MkPtr referer) {
        return hotlinkBlocked ? -1 : 0;
    }

    public static void resetRules() {
        blockedPrefixes.clear();
        hotlinkBlocked = false;
    }

    private MkSecurity() {}
}
