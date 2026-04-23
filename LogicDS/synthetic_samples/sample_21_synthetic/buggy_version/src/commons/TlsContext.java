package commons;

public class TlsContext {
    public TlsArrays arrays;
    public Options options;
    public Keys keys;
    public Session session;
    public java.util.List<TlsExtension> extensions;
    public TlsContext() {
        this.arrays = new TlsArrays();
        this.options = new Options();
        this.keys = new Keys();
        this.session = new Session();
    }
}
