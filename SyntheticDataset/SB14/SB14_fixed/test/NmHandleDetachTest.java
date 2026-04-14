import commons.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import main.NmHandleDetach;

public class NmHandleDetachTest {

    @Before
    public void setUp() {
        NodeManager.lastDetachedHandle = null;
        NodeManager.lastEnqueuedEvent  = null;
    }

    @Test
    public void testDetachWithCloseCallbackSchedulesEvent() {
        NmHandle handle = new NmHandle();
        NmSocket sock = new NmSocket();
        sock.tid           = NodeManager.nmTid();
        sock.closeCallback = true;
        Manager mgr = new Manager();
        mgr.workers = new Worker[1];
        mgr.workers[sock.tid] = new Worker();
        sock.mgr = mgr;

        handle.sock = sock;
        HandleRef ref = new HandleRef();
        ref.handle = handle;

        NmHandleDetach.isc__nmhandle_detach(ref);

        assertNull("Should NOT call direct detach_cb when closeCallback is present",
                NodeManager.lastDetachedHandle);

        assertNotNull("Should enqueue a detach event",
                NodeManager.lastEnqueuedEvent);
        assertSame("Enqueued event must carry the same handle",
                handle, NodeManager.lastEnqueuedEvent.handle);

        assertNull("Original HandleRef must be cleared", ref.handle);
    }
}
