package commons;

public class X509Name {
    private String name;
    public X509Name(String subj) {
        name = subj;
    }
    public X509Name(){
        name = "<name>";
    }

    @Override public String toString() { return name; }
}