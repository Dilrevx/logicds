package common;

public class X509 {
    public static final int EXFLAG_CA    = 0x01;
    public static final int EXFLAG_PROXY = 0x02;
    public static final int EXFLAG_SI    = 0x04;

    public int caCheckResult;
    public boolean explicitCurveParams;
    public int ex_flags;
    public int ex_pathlen;
    public int ex_pcpathlen;

    public X509(int caCheckResult, boolean explicitCurveParams, int ex_flags, int ex_pathlen) {
        this.caCheckResult = caCheckResult;
        this.explicitCurveParams = explicitCurveParams;
        this.ex_flags = ex_flags;
        this.ex_pathlen = ex_pathlen;
        this.ex_pcpathlen = -1;
    }
}
