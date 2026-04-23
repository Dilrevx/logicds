package commons;

import java.util.Objects;

public class JID {
    private String node;
    private String domain;
    private String resource;

    public JID(String node, String domain, String resource) {
        this.node = node;
        this.domain = domain == null ? "example.com" : domain;
        this.resource = resource;
    }

    public String getNode() {
        return node;
    }

    public String getDomain() {
        return domain;
    }
    
    public String getResource() {
        return resource;
    }

    public JID asBareJID() {
        return new JID(this.node, this.domain, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JID jid = (JID) o;
        boolean nodeEquals = (node != null ? node.equals(jid.node) : jid.node == null);
        boolean domainEquals = (domain != null ? domain.equals(jid.domain) : jid.domain == null);
        
        if (this.resource == null) {
             return nodeEquals && domainEquals && jid.resource == null;
        }
        return nodeEquals && domainEquals && (resource != null ? resource.equals(jid.resource) : jid.resource == null);
    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        if (this.resource == null) {
            return result;
        }
        return 31 * result + (resource != null ? resource.hashCode() : 0);
    }
    
    @Override
    public String toString() {
        return (node != null ? node + "@" : "") + domain + (resource != null ? "/" + resource : "");
    }
}