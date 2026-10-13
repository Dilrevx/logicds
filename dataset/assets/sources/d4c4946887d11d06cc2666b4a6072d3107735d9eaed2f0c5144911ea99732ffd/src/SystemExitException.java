public class SystemExitException extends RuntimeException {

    private final int code;

    public SystemExitException(int code) {
        super("exit(" + code + ")");
        this.code = code;
    }

    public int getCode() { return code; }
}
