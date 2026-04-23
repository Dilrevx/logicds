package common;

public class DnsZone {
    public String zoneName;
    public ZoneType zoneType;

    public DnsZone(String zoneName, ZoneType zoneType) {
        this.zoneName = zoneName;
        this.zoneType = zoneType;
    }
}
