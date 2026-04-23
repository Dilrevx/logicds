public class AuthError {

    public static final String UNAUTHORIZED_REASON = "unauthorized";

    public enum Kind { UNAUTHORIZED, FORBIDDEN }

    private final Kind kind;
    private final String reason;

    public AuthError(Kind kind, String reason) {
        this.kind = kind;
        this.reason = reason;
    }

    public static AuthError unauthorized(String reason) {
        return new AuthError(Kind.UNAUTHORIZED, reason);
    }

    public static AuthError forbidden(String reason) {
        return new AuthError(Kind.FORBIDDEN, reason);
    }

    public Kind getKind() { return kind; }
    public String getReason() { return reason; }
}
