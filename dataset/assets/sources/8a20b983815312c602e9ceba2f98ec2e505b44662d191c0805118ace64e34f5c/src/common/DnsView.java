package common;

import java.util.ArrayList;
import java.util.List;

public class DnsView {
    public String transfer_format;
    public List<String> dlz_searched;
    public Object zonetable;

    public DnsView(String transfer_format) {
        this.transfer_format = transfer_format;
        this.dlz_searched = new ArrayList<>();
        this.zonetable = new Object();
    }
}
