public class AuthContext {

    public NgxStr token = new NgxStr();
    public int head = 0;
    public int ret = NgxConst.NGX_HTTP_UNAUTHORIZED;
    public NgxStr tokenOutB64 = new NgxStr();

    public NgxStr getToken() { return token; }
    public int getHead() { return head; }
    public int getRet() { return ret; }
    public NgxStr getTokenOutB64() { return tokenOutB64; }
}
