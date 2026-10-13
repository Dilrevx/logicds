public final class AuthResult {

    private final String user;
    private final Exception err;

    public AuthResult(String user, Exception err) {
        this.user = user;
        this.err = err;
    }

    public String getUser() { return user; }
    public Exception getErr() { return err; }
}
