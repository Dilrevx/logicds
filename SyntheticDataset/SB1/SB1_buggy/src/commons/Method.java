package commons;

public class Method {
    public boolean lastOk;
    public long ssl_get_message(SSL s,
                                int a, int b, int c, int d,
                                boolean[] ok) {
        ok[0] = true;
        return 0;
    }
}