package commons;
public class Vctx {
    public Object   secroots;
    public byte[]   zsk_algorithms  = new byte[256];
    public byte[]   standby_zsk     = new byte[256];
    public boolean[] goodzsk        = new boolean[1];

    public byte[]   ksk_algorithms  = new byte[256];
    public byte[]   standby_ksk     = new byte[256];
    public boolean[] goodksk        = new boolean[1];

    public Object   origin;
    public Object   keyset, keysigs;
    public Object   soaset, soasigs;
    public Object   mctx;
}
