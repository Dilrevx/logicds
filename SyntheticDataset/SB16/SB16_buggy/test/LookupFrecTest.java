import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import commons.*;
import main.LookupFrec;

public class LookupFrecTest {

    private static final int ID = 123;
    private static final byte[] HASH = new byte[]{1, 2, 3, 4};

    @Before
    public void setUp() {
        Daemon.frec_list = null;
    }

    @Test
    public void testDoesNotMatchWithWrongFd() {
        Frec f = new Frec();
        f.new_id = ID;
        f.hash = HASH;
        f.sentto = new SentTo();
        f.sentto.sfd = new SocketFd();
        f.sentto.sfd.fd = 777;
        f.rfd4 = new Rfd4(); f.rfd4.fd = 111;
        f.rfd6 = new Rfd6(); f.rfd6.fd = 222;
        Daemon.frec_list = f;

        Frec out = LookupFrec.lookup_frec(ID, HASH, Commons.AF_INET, 999);
        assertNull("Lookup with wrong fd should return null", out);
    }

    @Test
    public void testDoesNotMatchWithWrongFd2() {
        Frec f = new Frec();
        f.new_id = ID;
        f.hash = HASH;
        f.sentto = new SentTo();
        f.sentto.sfd = new SocketFd();
        f.sentto.sfd.fd = 777;
        f.rfd4 = new Rfd4(); f.rfd4.fd = 111;
        f.rfd6 = new Rfd6(); f.rfd6.fd = 222;
        Daemon.frec_list = f;

        Frec out = LookupFrec.lookup_frec(ID, HASH, Commons.AF_INET6, 999);
        assertNull("Lookup with wrong fd should return null", out);
    }

    @Test
    public void testIpv4FdMatches() {
        Frec f = new Frec();
        f.new_id = ID;
        f.hash = HASH;
        f.sentto = new SentTo();
        f.sentto.sfd = new SocketFd();
        f.sentto.sfd.fd = 777;
        f.rfd4 = new Rfd4(); f.rfd4.fd = 111;
        Daemon.frec_list = f;

        Frec out = LookupFrec.lookup_frec(ID, HASH, Commons.AF_INET, 111);
        assertSame("Lookup should match on IPv4 fd", f, out);
    }

    @Test
    public void testIpv6FdMatches() {
        Frec f = new Frec();
        f.new_id = ID;
        f.hash = HASH;
        f.sentto = new SentTo();
        f.sentto.sfd = new SocketFd();
        f.sentto.sfd.fd = 777;
        f.rfd6 = new Rfd6(); f.rfd6.fd = 222;
        Daemon.frec_list = f;

        Frec out = LookupFrec.lookup_frec(ID, HASH, Commons.AF_INET6, 222);
        assertSame("Lookup should match on IPv6 fd", f, out);
    }

    @Test
    public void testSentToFdMatches() {
        Frec f = new Frec();
        f.new_id = ID;
        f.hash = HASH;
        f.rfd4 = new Rfd4(); f.rfd4.fd = 111;
        f.rfd6 = new Rfd6(); f.rfd6.fd = 222;
        f.sentto = new SentTo();
        f.sentto.sfd = new SocketFd();
        f.sentto.sfd.fd = 777;
        Daemon.frec_list = f;

        Frec out = LookupFrec.lookup_frec(ID, HASH, Commons.AF_INET, 777);
        assertSame("Lookup should match on sentto socket fd", f, out);
    }
}
