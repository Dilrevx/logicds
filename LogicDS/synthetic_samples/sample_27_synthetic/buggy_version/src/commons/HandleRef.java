package commons;
public class HandleRef {
    public NmHandle handle;
    public static boolean valid(HandleRef ref) {
        return ref != null && ref.handle != null;
    }
}