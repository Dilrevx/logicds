package commons;

public class DstKey {
    public final int alg, id;
    public final byte[] material;
    public DstKey(int alg, int id, byte[] material) {
        this.alg = alg; this.id = id; this.material = material;
    }
    @Override public boolean equals(Object o) {
        if (!(o instanceof DstKey)) return false;
        DstKey d = (DstKey)o;
        if (d.alg!=alg || d.id!=id || d.material.length!=material.length) return false;
        for (int i=0;i<material.length;i++) if (d.material[i]!=material[i]) return false;
        return true;
    }
}
