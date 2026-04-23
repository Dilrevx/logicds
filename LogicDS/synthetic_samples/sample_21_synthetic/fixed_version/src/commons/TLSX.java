package commons;

import java.util.List;

public class TLSX {
    public static TlsExtension find(List<TlsExtension> exts, int type) {
        if (exts == null) return null;
        for (TlsExtension ext : exts) {
            if (ext != null && ext.data instanceof PreSharedKey) {
                return ext;
            }
        }
        return null;
    }
}
