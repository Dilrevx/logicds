import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AuthCheckerTest {

    private AuthChecker checker;
    private Kadm5Handle handle;

    @Before
    public void setUp() {
        checker = new AuthChecker();
        handle = new Kadm5Handle();
        handle.setRealm("EXAMPLE.COM");
        handle.setContext(new Object());
        checker.setGlobalServerHandle(handle);
    }

    private SvcReq buildRequest(String realmBytes, int realmLen,
                                String c1Bytes, int c1Len,
                                String c2Bytes, int c2Len) {
        KrbPrincipal princ = new KrbPrincipal();
        princ.setRealm(new KrbData(realmBytes.getBytes(), realmLen));
        KrbData[] comps = new KrbData[]{
                new KrbData(c1Bytes.getBytes(), c1Len),
                new KrbData(c2Bytes.getBytes(), c2Len)
        };
        princ.setComponents(comps);

        SvcReq req = new SvcReq();
        req.setOaFlavor(AuthChecker.RPCSEC_GSS);
        req.setSvcCred(new Object());
        req.setPreloadedName("kadmin/admin@EXAMPLE.COM");
        req.setPreloadedPrincipal(princ);
        return req;
    }

    @Test
    public void testExactKadminAdminAccepted() {
        SvcReq req = buildRequest("EXAMPLE.COM", 11, "kadmin", 6, "admin", 5);
        int ret = checker.checkRpcsecAuth(req);
        assertEquals("exact match principal should succeed", 1, ret);
    }

    @Test
    public void testPrefixPrincipalRejected() {
        SvcReq req = buildRequest("EX", 2, "k", 1, "admin", 5);
        int ret = checker.checkRpcsecAuth(req);
        assertEquals("prefix-matching principal must be rejected", 0, ret);
    }
}
