package commons;

public class X509VerifyCtx {
    public int chains_count;
    public int max_chains;
    public X509VerifyChain[] chains;
    public int error;
    public int error_depth;
}
