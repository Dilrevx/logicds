public class ConfigLoader {

    public static final class LoadResult {
        public final WebConfig config;
        public final Exception err;
        public LoadResult(WebConfig config, Exception err) {
            this.config = config;
            this.err = err;
        }
    }

    public static WebConfig preloadedConfig;
    public static Exception preloadedErr;

    public static LoadResult getConfig(String path) {
        return new LoadResult(preloadedConfig, preloadedErr);
    }

    public static void reset() {
        preloadedConfig = null;
        preloadedErr = null;
    }

    private ConfigLoader() {}
}
