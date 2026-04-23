package commons;

public class X509Revoked {
    private SerialNumber serialNumber;
    public X509Revoked(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }

    public SerialNumber getSerialNumber() { return serialNumber; }
}