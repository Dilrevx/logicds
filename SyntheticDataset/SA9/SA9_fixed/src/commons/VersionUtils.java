package commons;

public class VersionUtils {
    private static VersionEntry lowestVersion;
    private static VersionEntry maxVersion;
    private static VersionEntry legacyMaxVersion;
    
    public static VersionEntry versionLowest(Session session) {
        return lowestVersion;
    }
    
    public static VersionEntry versionMax(Session session) {
        return maxVersion;
    }
    
    public static VersionEntry legacyVersionMax(Session session) {
        return legacyMaxVersion;
    }
    
    public static boolean versionHasExtensions(VersionEntry version) {
        return version.getId() != Constants.GNUTLS_SSL3;
    }
    
    public static void setVersionLowest(VersionEntry version) {
        lowestVersion = version;
    }
    
    public static void setVersionMax(VersionEntry version) {
        maxVersion = version;
    }
    
    public static void setLegacyVersionMax(VersionEntry version) {
        legacyMaxVersion = version;
    }
}
