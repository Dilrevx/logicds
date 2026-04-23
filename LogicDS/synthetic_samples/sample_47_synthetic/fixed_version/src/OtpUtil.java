public class OtpUtil {

    public static int decodeKrb5PaOtpReqRet = 0;
    public static int decryptEncdataRet = 0;
    public static int nonceVerifyRet = 0;
    public static int timestampVerifyRet = 0;

    public static KrbPaData makeData(byte[] contents, int length) {
        return new KrbPaData(contents, length);
    }

    public static int decodeKrb5PaOtpReq(KrbPaData d, KrbPaOtpReq[] out) {
        if (decodeKrb5PaOtpReqRet != 0) {
            return decodeKrb5PaOtpReqRet;
        }
        if (d == null || d.getContents() == null || d.getLength() <= 0) {
            return 1;
        }
        KrbPaOtpReq req = new KrbPaOtpReq();
        req.setEncData(d.getContents());
        out[0] = req;
        return 0;
    }

    public static int decryptEncdata(Object context, Object armorKey, KrbPaOtpReq req, KrbPaData plaintextOut) {
        if (decryptEncdataRet != 0) {
            return decryptEncdataRet;
        }
        if (armorKey == null || req == null) {
            return 1;
        }
        plaintextOut.setContents(req.getEncData());
        plaintextOut.setLength(req.getEncData() == null ? 0 : req.getEncData().length);
        return 0;
    }

    public static int nonceVerify(Object context, Object armorKey, KrbPaData plaintext) {
        return nonceVerifyRet;
    }

    public static int timestampVerify(Object context, KrbPaData plaintext) {
        return timestampVerifyRet;
    }

    public static void krb5FreeDataContents(Object context, KrbPaData data) {
    }

    public static RequestState k5alloc(int[] retvalOut) {
        retvalOut[0] = 0;
        return new RequestState();
    }

    public static void otpStateVerify(Object moddata, Object eventContext, Object client,
                                      String config, KrbPaOtpReq req,
                                      OnResponseFn onResponse, RequestState rs) {
        if (onResponse != null) {
            onResponse.onResponse(rs, 0, null, null, null);
        }
    }

    public static void comErr(String mod, int retval, String fmt, Object... args) {
    }

    public static void k5FreePaOtpReq(Object context, KrbPaOtpReq req) {
    }

    public static void resetMocks() {
        decodeKrb5PaOtpReqRet = 0;
        decryptEncdataRet = 0;
        nonceVerifyRet = 0;
        timestampVerifyRet = 0;
    }

    private OtpUtil() {}
}
