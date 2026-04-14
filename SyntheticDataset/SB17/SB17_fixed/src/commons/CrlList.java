package commons;

public class CrlList {
    private final java.util.List<Crl> inner = new java.util.ArrayList<Crl>();
    public void add(Crl c) { inner.add(c); }
    public int size()            { return inner.size(); }
    public Crl get(int i)       { return inner.get(i); }
    public void free()           { }
}