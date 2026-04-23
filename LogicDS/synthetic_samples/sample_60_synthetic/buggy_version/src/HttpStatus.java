public class HttpStatus {
    public static final int OK                    = 200;
    public static final int UNAUTHORIZED          = 401;
    public static final int INTERNAL_SERVER_ERROR = 500;

    public static String statusText(int status) {
        switch (status) {
            case OK: return "OK";
            case UNAUTHORIZED: return "Unauthorized";
            case INTERNAL_SERVER_ERROR: return "Internal Server Error";
            default: return "Status " + status;
        }
    }

    private HttpStatus() {}
}
