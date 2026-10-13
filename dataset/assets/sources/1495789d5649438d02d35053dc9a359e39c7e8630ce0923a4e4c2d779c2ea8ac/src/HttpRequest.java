public class HttpRequest {

    public static final class BasicAuthResult {
        public final String user;
        public final String pass;
        public final boolean present;

        public BasicAuthResult(String user, String pass, boolean present) {
            this.user = user;
            this.pass = pass;
            this.present = present;
        }
    }

    private String user;
    private String pass;
    private boolean authPresent;

    public void setBasicAuth(String user, String pass) {
        this.user = user;
        this.pass = pass;
        this.authPresent = true;
    }

    public BasicAuthResult basicAuth() {
        return new BasicAuthResult(user == null ? "" : user,
                                   pass == null ? "" : pass,
                                   authPresent);
    }
}
