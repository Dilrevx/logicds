package commons;

public class OSSL_PARAM {
    public String name;
    public byte[] data;
    public int data_size;
    public String data_type;

    public OSSL_PARAM(String name, byte[] data, String data_type) {
        this.name = name;
        this.data = data;
        this.data_size = data != null ? data.length : 0;
        this.data_type = data_type;
    }

    public boolean is_octet_string() {
        return "OCTET_STRING".equals(data_type);
    }

    public static class SizeTHolder {
        public boolean isValid;
        public int value;
    }

    public static class UIntHolder {
        public boolean isValid;
        public int value;
    }

    public SizeTHolder get_size_t_holder() {
        SizeTHolder h = new SizeTHolder();
        h.isValid = data != null;
        h.value = data_size;
        return h;
    }

    public UIntHolder get_uint_holder() {
        UIntHolder h = new UIntHolder();
        h.isValid = true;
        h.value = 0x0303;
        return h;
    }
}
