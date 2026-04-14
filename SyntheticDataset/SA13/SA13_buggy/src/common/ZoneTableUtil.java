package common;

public class ZoneTableUtil {

    public static DnsResult dnsZtFind(Object zonetable, String questionName, int flags, Object ignore, DnsZone zoneOut) {
        if (zonetable != null && questionName != null) {
            return DnsResult.ISC_R_SUCCESS;
        } else {
            return DnsResult.ISC_R_NOTFOUND;
        }
    }
}
