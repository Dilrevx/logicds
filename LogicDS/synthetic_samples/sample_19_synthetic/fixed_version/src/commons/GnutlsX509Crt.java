package commons;

public class GnutlsX509Crt {
    public GnutlsDatum der = new GnutlsDatum();
    public boolean expanded;
    public Object cert;
    public GnutlsDatum rawIssuerDn = new GnutlsDatum();
    public GnutlsDatum rawDn         = new GnutlsDatum();
    public GnutlsDatum rawSpki       = new GnutlsDatum();
    public boolean useExtensions;
}
