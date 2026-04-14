package commons;

public class NodeManager {
    public static NmHandle lastDetachedHandle;
    public static NetiEventDetach lastEnqueuedEvent;

    public static int nmTid() { return 0; }
    public static boolean hasCloseHandleCb(NmSocket s) {
        return s.closeCallback;
    }
    public static void nmhandle_detach_cb(NmHandle h) {
        lastDetachedHandle = h;
    }
    public static NetiEventDetach getNetiEventDetach(Manager m, NmSocket s) {
        return new NetiEventDetach();
    }
    public static void enqueueEvent(Worker w, NetiEventDetach e) {
        lastEnqueuedEvent = e;
    }
}
