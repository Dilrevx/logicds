public class SvcReq {

    private int oaFlavor;
    private Object svcCred;
    private Object rqXprt;

    private String preloadedName;
    private KrbPrincipal preloadedPrincipal;
    private GssBufferDesc preloadedGssStr = new GssBufferDesc();
    private int preloadedInquireStat = KadmUtil.GSS_S_COMPLETE;
    private int preloadedToKrb5NameStat = 1;

    public int getOaFlavor() { return oaFlavor; }
    public void setOaFlavor(int oaFlavor) { this.oaFlavor = oaFlavor; }

    public Object getSvcCred() { return svcCred; }
    public void setSvcCred(Object svcCred) { this.svcCred = svcCred; }

    public Object getRqXprt() { return rqXprt; }
    public void setRqXprt(Object rqXprt) { this.rqXprt = rqXprt; }

    public String getPreloadedName() { return preloadedName; }
    public void setPreloadedName(String preloadedName) { this.preloadedName = preloadedName; }

    public KrbPrincipal getPreloadedPrincipal() { return preloadedPrincipal; }
    public void setPreloadedPrincipal(KrbPrincipal p) { this.preloadedPrincipal = p; }

    public GssBufferDesc getPreloadedGssStr() { return preloadedGssStr; }
    public void setPreloadedGssStr(GssBufferDesc g) { this.preloadedGssStr = g; }

    public int getPreloadedInquireStat() { return preloadedInquireStat; }
    public void setPreloadedInquireStat(int v) { this.preloadedInquireStat = v; }

    public int getPreloadedToKrb5NameStat() { return preloadedToKrb5NameStat; }
    public void setPreloadedToKrb5NameStat(int v) { this.preloadedToKrb5NameStat = v; }
}
