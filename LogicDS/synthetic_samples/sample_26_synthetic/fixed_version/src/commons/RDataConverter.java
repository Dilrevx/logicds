package commons;

public class RDataConverter {
    public static int testError = Commons.dns_rcode_noerror;

    public static int toStruct(RData rdata, Object struct) {
        if (struct instanceof TsigRecord) {
            ((TsigRecord) struct).error = testError;
        }
        return Commons.ISC_R_SUCCESS;
    }

    public static void freeStruct(Object struct) { }
}
