public class AuthService {

    public static final class CacheResult {
        public final UserInfo userInfo;
        public final Exception err;
        public CacheResult(UserInfo userInfo, Exception err) {
            this.userInfo = userInfo;
            this.err = err;
        }
    }

    private UserInfo preloadedUserInfo;
    private Exception preloadedErr;

    public CacheResult getAdminUserCacheInfo(GinContext ctx, String token) {
        return new CacheResult(preloadedUserInfo, preloadedErr);
    }

    public void setPreloadedUserInfo(UserInfo userInfo) { this.preloadedUserInfo = userInfo; }
    public void setPreloadedErr(Exception err) { this.preloadedErr = err; }
}
