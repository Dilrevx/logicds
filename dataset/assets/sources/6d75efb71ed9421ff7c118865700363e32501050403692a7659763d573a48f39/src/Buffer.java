public class Buffer {

    public String buf;
    public int len;

    public Buffer() {
        this.buf = "";
        this.len = 0;
    }

    public Buffer(String s) {
        this.buf = s == null ? "" : s;
        this.len = this.buf.length();
    }

    public boolean isEmpty() {
        return len == 0;
    }

    public int countCspn(int start, String stopChars) {
        if (buf == null) return 0;
        int n = 0;
        for (int i = start; i < buf.length(); i++) {
            if (stopChars.indexOf(buf.charAt(i)) >= 0) {
                return n;
            }
            n++;
        }
        return n;
    }
}
