public class KrbPaData {

    private byte[] contents;
    private int length;

    public KrbPaData() {}

    public KrbPaData(byte[] contents, int length) {
        this.contents = contents;
        this.length = length;
    }

    public byte[] getContents() { return contents; }
    public void setContents(byte[] contents) { this.contents = contents; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
}
