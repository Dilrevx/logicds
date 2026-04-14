package common;

public class SSHSession {
    private boolean isClient;
    private boolean isServer;
    private SSHCrypto nextCrypto;

    public SSHSession(boolean isClient, boolean isServer) {
        this.isClient = isClient;
        this.isServer = isServer;
        this.nextCrypto = new SSHCrypto();
    }

    public SSHCrypto getNextCrypto() {
        return nextCrypto;
    }

    public boolean isClient() {
        return isClient;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setError(String message) {
        System.err.println("Error: " + message);
    }
}