import org.junit.Test;
import static org.junit.Assert.*;

import common.DnsClient;
import common.DnsResult;
import common.DnsRdatatype;
import common.DnsZone;
import common.DnsView;
import common.ZoneType;

import master.*;

public class ZoneXferTest {

    @Test
    public void testDlzZoneAllowed() {
        DnsView view = new DnsView("dummyFormat");
        view.zonetable = new Object();
        view.dlz_searched.add("dlz-db1");
        DnsClient client = new DnsClient("10.53.0.10", "10.53.0.10", view);
        DnsZone zone = new DnsZone("example.nil", ZoneType.DNS_ZONE_DLZ);

        DnsResult result = XfrOut.nsXfrStart(client, zone, DnsRdatatype.AXFR);
        assertEquals("DLZ zone allowed should return ISC_R_SUCCESS", DnsResult.ISC_R_SUCCESS, result);
    }

    @Test
    public void testDlzZoneDisallowed() {
        DnsView view = new DnsView("dummyFormat");
        view.zonetable = new Object();
        view.dlz_searched.add("dlz-db1");

        DnsClient client = new DnsClient("10.53.0.5", "10.53.0.5", view);
        DnsZone zone = new DnsZone("example.nil", ZoneType.DNS_ZONE_DLZ);

        DnsResult result = XfrOut.nsXfrStart(client, zone, DnsRdatatype.AXFR);
        assertEquals("DLZ zone disallowed should return ISC_R_NOPERM", DnsResult.ISC_R_NOPERM, result);
    }

    @Test
    public void testUnknownZone() {
        DnsView view = new DnsView("dummyFormat");
        view.zonetable = new Object();
        view.dlz_searched.add("dlz-db1");

        DnsClient client = new DnsClient("10.53.0.10", "10.53.0.10", view);
        DnsZone zone = new DnsZone("notfound.nil", ZoneType.DNS_ZONE_DLZ);

        DnsResult result = XfrOut.nsXfrStart(client, zone, DnsRdatatype.AXFR);
        assertEquals("Unknown zone should return ISC_R_NOTFOUND", DnsResult.ISC_R_NOTFOUND, result);
    }

    @Test
    public void testNormalZone() {
        DnsView view = new DnsView("dummyFormat");
        view.zonetable = new Object();
        DnsClient client = new DnsClient("10.53.0.10", "10.53.0.10", view);
        DnsZone zone = new DnsZone("example.com", ZoneType.DNS_ZONE_MASTER);
        DnsResult result = XfrOut.nsXfrStart(client, zone, DnsRdatatype.AXFR);
        assertEquals("Normal zone transfer should return ISC_R_SUCCESS", DnsResult.ISC_R_SUCCESS, result);
    }

}
