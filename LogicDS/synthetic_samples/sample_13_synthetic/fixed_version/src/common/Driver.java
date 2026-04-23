package common;
public class Driver {

    public static DnsResult dlzAllowZonexfr(DbData dbdata, String zoneName, String clientIp) {
        if (!"example.nil".equalsIgnoreCase(zoneName)) {
            return DnsResult.ISC_R_NOTFOUND;
        }

        if ("10.53.0.5".equals(clientIp)) {
            return DnsResult.ISC_R_NOPERM;
        }
        return DnsResult.ISC_R_SUCCESS;
    }
}
