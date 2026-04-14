package commons;

public class XMPPServer {
    private static XMPPServer instance = new XMPPServer();
    private String serverDomain = "example.com";

    private XMPPServer() {}

    public static XMPPServer getInstance() {
        if (instance == null) {
            instance = new XMPPServer();
        }
        return instance;
    }

    public JID createJID(String username, String resource) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty for admin JID");
        }
        return new JID(username.toLowerCase(), serverDomain, resource);
    }
    
    public static void setInstance(XMPPServer testInstance) {
        instance = testInstance;
    }
}