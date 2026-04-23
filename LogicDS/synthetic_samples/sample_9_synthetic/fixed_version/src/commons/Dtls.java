package commons;

public class Dtls {
    private Datum dcookie;
    private int hskHelloVerifyRequests;
    
    public Datum getDcookie() {
        return dcookie;
    }

    public void setHskHelloVerifyRequests(int hskHelloVerifyRequests) {
        this.hskHelloVerifyRequests = hskHelloVerifyRequests;
    }

    public int getHskHelloVerifyRequests() {
        return hskHelloVerifyRequests;
    }
    
    public void freeDcookie() {
    }

    public void setDcookie(Datum dcookie) {
        this.dcookie = dcookie;
    }
}
