package common;

public class SSHCrypto {
    private SSHKex serverKex = new SSHKex(true);
    private SSHKex clientKex = new SSHKex(false);
    private String[] kexMethods = new String[SSHConstants.SSH_KEX_METHODS];

    public SSHKex getServerKex() {
        return serverKex;
    }

    public SSHKex getClientKex() {
        return clientKex;
    }

    public void setKexMethod(int index, String method) {
        kexMethods[index] = method;
    }

    public String getKexMethod(int index) {
        return kexMethods[index];
    }
}