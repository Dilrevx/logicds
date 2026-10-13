public class MountCall {

    public final String source;
    public final String target;
    public final String fstype;
    public final int flags;

    public MountCall(String source, String target, String fstype, int flags) {
        this.source = source;
        this.target = target;
        this.fstype = fstype;
        this.flags = flags;
    }

    public String getSource() { return source; }
    public String getTarget() { return target; }
    public String getFstype() { return fstype; }
    public int getFlags() { return flags; }
}
