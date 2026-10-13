public class MkPtr {

    public String data;
    public int len;

    public MkPtr() {}

    public MkPtr(String data) {
        this.data = data == null ? "" : data;
        this.len = this.data.length();
    }
}
