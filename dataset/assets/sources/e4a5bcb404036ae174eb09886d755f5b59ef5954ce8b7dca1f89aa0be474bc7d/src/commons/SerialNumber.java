package commons;

public class SerialNumber {
    private final long value;
    public SerialNumber(long v) { value = v; }
    public SerialNumber(String s) { value = Long.parseLong(s); }
    @Override public boolean equals(Object o) {
        return (o instanceof SerialNumber) && ((SerialNumber)o).value == value;
    }
    @Override public String toString() { return Long.toString(value); }
}