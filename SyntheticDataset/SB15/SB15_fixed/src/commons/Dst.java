package commons;
public class Dst {
    public static int dst_key_alg(DstKey k) { return k.alg; }
    public static int dst_key_id(DstKey k)  { return k.id; }
    public static boolean dst_key_compare(DstKey a, Keynode node) {
        return a.equals(node.key);
    }
    public static void dst_key_free(DstKey[] k) { }
}
