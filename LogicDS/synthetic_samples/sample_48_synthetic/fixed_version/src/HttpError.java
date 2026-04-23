public class HttpError {

    private final int code;
    private final String message;
    private Throwable internal;

    public HttpError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }

    public Throwable getInternal() { return internal; }
    public HttpError setInternal(Throwable t) {
        this.internal = t;
        return this;
    }
}
