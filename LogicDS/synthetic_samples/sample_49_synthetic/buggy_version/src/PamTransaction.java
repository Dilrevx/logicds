public class PamTransaction {

    public static Exception startFuncError = null;
    public static PamTransaction preloadedTransaction = null;

    public Exception authenticateError;
    public Exception acctMgmtError;
    public String itemUser = "";
    public Exception getItemError;

    private String serviceName;
    private String userName;
    private PamConv conv;

    public PamTransaction() {
    }

    public PamTransaction(String serviceName, String userName, PamConv conv) {
        this.serviceName = serviceName;
        this.userName = userName;
        this.conv = conv;
    }

    public static PamStartResult startFunc(String serviceName, String userName, PamConv conv) {
        if (startFuncError != null) {
            return new PamStartResult(null, startFuncError);
        }
        if (preloadedTransaction != null) {
            preloadedTransaction.serviceName = serviceName;
            preloadedTransaction.userName = userName;
            preloadedTransaction.conv = conv;
            return new PamStartResult(preloadedTransaction, null);
        }
        return new PamStartResult(new PamTransaction(serviceName, userName, conv), null);
    }

    public Exception authenticate(int flags) {
        return authenticateError;
    }

    public Exception acctMgmt(int flags) {
        return acctMgmtError;
    }

    public ItemResult getItem(int item) {
        return new ItemResult(itemUser, getItemError);
    }

    public String getServiceName() { return serviceName; }
    public String getUserName() { return userName; }
    public PamConv getConv() { return conv; }

    public static final class PamStartResult {
        public final PamTransaction t;
        public final Exception err;
        public PamStartResult(PamTransaction t, Exception err) {
            this.t = t;
            this.err = err;
        }
    }

    public static final class ItemResult {
        public final String value;
        public final Exception err;
        public ItemResult(String value, Exception err) {
            this.value = value;
            this.err = err;
        }
    }

    public static void resetMocks() {
        startFuncError = null;
        preloadedTransaction = null;
    }
}
