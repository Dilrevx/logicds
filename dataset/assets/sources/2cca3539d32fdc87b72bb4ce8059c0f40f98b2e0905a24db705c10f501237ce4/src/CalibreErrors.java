public class CalibreErrors {

    public static class OperationalError extends Exception {
        public OperationalError(String msg) { super(msg); }
    }

    public static class InvalidRequestError extends Exception {
        public InvalidRequestError(String msg) { super(msg); }
    }

    private CalibreErrors() {}
}
