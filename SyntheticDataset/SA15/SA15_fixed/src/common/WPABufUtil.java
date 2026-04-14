package common;

public class WPABufUtil {
    public static WPABuf eapTlvBuildResult(EapSm sm, EapPeapData data, boolean cryptoBinding, int id, int respStatus) {
        return new WPABuf("Result: " + respStatus);
    }
}
