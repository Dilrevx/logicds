import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import commons.*;

import main.KeyVerifier;

public class KeyVerifierTest {

    private Vctx vctx;
    private DnsRdataDnskey dnskey;
    private RData rdata;

    @Before
    public void setUp() {
        vctx = new Vctx();
        vctx.secroots = new Object();
        dnskey = new DnsRdataDnskey();
        dnskey.algorithm = 42;
        rdata = new RData();
        rdata.key = new DstKey(42, 7, new byte[]{1,2,3});
        Dnssec.selfSigns = false;
        Dnssec.signs    = false;
    }

    @Test
    public void testMismatchedAnchorIsRejected() {
        Keytable.findResult    = Commons.ISC_R_SUCCESS;
        Keytable.findNode      = new Keynode(new DstKey(42,7,new byte[]{9,9,9}));
        Keytable.nextFindResult= Commons.ISC_R_NOTFOUND;

        vctx.goodzsk = new boolean[]{false};

        int rc = KeyVerifier.check_dnskey_sigs(
                vctx, dnskey, rdata, false
        );

        assertEquals(Commons.ISC_R_SUCCESS, rc);
        assertFalse("Mismatched anchor should NOT be accepted", vctx.goodzsk[0]);
    }

    @Test
    public void testMatchingAnchorIsAccepted() {
        Keytable.findResult    = Commons.ISC_R_SUCCESS;
        Keytable.findNode      = new Keynode(rdata.key);
        Keytable.nextFindResult= Commons.ISC_R_NOTFOUND;

        vctx.goodzsk = new boolean[]{false};

        int rc = KeyVerifier.check_dnskey_sigs(
                vctx, dnskey, rdata, false
        );

        assertEquals(Commons.ISC_R_SUCCESS, rc);
        assertTrue("Matching anchor must be accepted", vctx.goodzsk[0]);
    }
}
