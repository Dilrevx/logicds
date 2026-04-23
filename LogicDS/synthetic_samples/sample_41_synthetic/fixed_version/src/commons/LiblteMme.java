package commons;

public class LiblteMme {
    public static void parseMsgSecHeader(byte[] raw, RefHolder pd, RefHolder secHdr) {
        secHdr.value = raw[0];
    }

    public static void parseMsgHeader(byte[] raw, RefHolder pd, RefHolder msgType) {
        msgType.value = raw[1];
    }
}
