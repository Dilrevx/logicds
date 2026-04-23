public class LdapContext {

    public Object conn = new Object();
    public Buffer filter = new Buffer();
    public LdapProps props = new LdapProps();

    public Object getConn() { return conn; }
    public Buffer getFilter() { return filter; }
    public LdapProps getProps() { return props; }
}
