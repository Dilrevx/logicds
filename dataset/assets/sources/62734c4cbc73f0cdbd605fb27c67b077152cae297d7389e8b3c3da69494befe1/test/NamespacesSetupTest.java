import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class NamespacesSetupTest {

    private NamespacesSetup setup;
    private LoData lo;
    private FuseSession se;

    @Before
    public void setUp() {
        OsApi.resetMocks();
        OsApi.forkResult = 0;
        OsApi.openResult = 11;

        setup = new NamespacesSetup();
        lo = new LoData();
        se = new FuseSession();
    }

    @Test
    public void testProcSelfFdIsOpened() {
        setup.setupNamespaces(lo, se);

        assertEquals("proc_self_fd should be set to the open() return value",
                11, lo.getProcSelfFd());
    }

    @Test
    public void testNoTmpdirBindMount() {
        setup.setupNamespaces(lo, se);

        assertEquals("no user-visible tmpdir must be created",
                0, OsApi.mkdtempCalls.size());
        assertEquals("no tmpdir unmount/teardown must be needed",
                0, OsApi.umount2Calls.size());
        MountCall lastFdBind = null;
        for (int i = 0; i < OsApi.mountCalls.size(); i++) {
            MountCall m = OsApi.mountCalls.get(i);
            if ("/proc/self/fd".equals(m.getSource())) {
                lastFdBind = m;
            }
        }
        assertNotNull("a /proc/self/fd bind mount is expected", lastFdBind);
        assertEquals("/proc/self/fd must be bind-mounted directly to /proc",
                "/proc", lastFdBind.getTarget());
    }
}
