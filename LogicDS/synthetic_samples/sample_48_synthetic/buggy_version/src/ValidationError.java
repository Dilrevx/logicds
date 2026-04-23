public class ValidationError extends RuntimeException {

    public static final int VALIDATION_ERROR_EXPIRED   = 1 << 0;
    public static final int VALIDATION_ERROR_SIGNATURE = 1 << 1;
    public static final int VALIDATION_ERROR_MALFORMED = 1 << 2;

    private final int errors;

    public ValidationError(int errors, String message) {
        super(message);
        this.errors = errors;
    }

    public int getErrors() { return errors; }
}
