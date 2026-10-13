public class VerifyResult {

    public final boolean isCorrect;
    public final Exception err;

    public VerifyResult(boolean isCorrect, Exception err) {
        this.isCorrect = isCorrect;
        this.err = err;
    }

    public boolean isCorrect() { return isCorrect; }
    public Exception getErr() { return err; }
}
