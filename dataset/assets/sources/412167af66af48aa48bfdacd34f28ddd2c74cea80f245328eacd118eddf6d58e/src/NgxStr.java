public class NgxStr {

    public byte[] data;
    public int len;

    public NgxStr() {}

    public NgxStr(byte[] data, int len) {
        this.data = data;
        this.len = len;
    }

    public static NgxStr fromString(String s) {
        if (s == null) return new NgxStr();
        byte[] b = s.getBytes();
        return new NgxStr(b, b.length);
    }

    public String asString() {
        if (data == null) return "";
        return new String(data, 0, len);
    }
}
