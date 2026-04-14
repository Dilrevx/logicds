package common;

public class SSHKex {

    private Boolean isServer;
    private String[] methods;


    public SSHKex( Boolean isServer ) {
        this.isServer = isServer;
        this.methods  = new String[SSHConstants.SSH_KEX_METHODS] ;
        String serverkex = "server1,server2,server3," + SSHConstants.KEX_STRICT_SERVER;
        String clientkex = "client1,client2,client3," + SSHConstants.KEX_STRICT_SERVER;

        System.arraycopy(SSHConstants.KEX_DESCRIPTIONS, 0, methods, 0, SSHConstants.SSH_KEX_METHODS);

        if ( isServer ) {
            methods[SSHConstants.SSH_KEX] = serverkex;
        } else {
            methods[SSHConstants.SSH_KEX] = clientkex;
        }
    }

    public String[] getMethods() {
        return methods;
    }
}