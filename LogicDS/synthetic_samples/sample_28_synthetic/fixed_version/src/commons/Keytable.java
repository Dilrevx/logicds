package commons;
public class Keytable {
    public static int   findResult     = Commons.ISC_R_SUCCESS;
    public static Keynode findNode     = null;
    public static int   nextFindResult = Commons.ISC_R_NOTFOUND;
    public static Keynode nextFindNode = null;

    public static int dns_keytable_findkeynode(Object roots, Object origin,
                                               int alg, int id,
                                               Keynode[] out)
    {
        out[0] = findNode;
        return findResult;
    }
    public static int dns_keytable_findnextkeynode(Object roots,
                                                   Keynode current,
                                                   Keynode[] out)
    {
        out[0] = nextFindNode;
        return nextFindResult;
    }
    public static void dns_keytable_detachkeynode(Object roots, Keynode[] nodes) { }
    public static Keynode keyOfNode(Keynode node) {
        return node;
    }
}





