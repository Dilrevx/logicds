public class KdcPreauthCallbacks {

    private Object preloadedArmorKey;
    private String preloadedConfig;
    private int preloadedGetStringRet;
    private Object preloadedEventContext;

    public Object fastArmor(Object context, Object rock) {
        return preloadedArmorKey;
    }

    public int getString(Object context, Object rock, String key, String[] out) {
        out[0] = preloadedConfig;
        return preloadedGetStringRet;
    }

    public Object eventContext(Object context, Object rock) {
        return preloadedEventContext;
    }

    public void freeString(Object context, Object rock, String s) {
    }

    public void setPreloadedArmorKey(Object key) { this.preloadedArmorKey = key; }
    public void setPreloadedConfig(String config) { this.preloadedConfig = config; }
    public void setPreloadedGetStringRet(int ret) { this.preloadedGetStringRet = ret; }
    public void setPreloadedEventContext(Object eventContext) { this.preloadedEventContext = eventContext; }
}
