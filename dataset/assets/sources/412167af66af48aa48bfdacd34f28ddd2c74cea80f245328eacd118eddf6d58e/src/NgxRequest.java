public class NgxRequest {

    private NgxHeaders headersIn = new NgxHeaders();
    private Object pool = new Object();

    public NgxHeaders getHeadersIn() { return headersIn; }

    public Object getPool() { return pool; }
}
