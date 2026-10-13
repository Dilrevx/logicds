package commons;

public enum INTEGRITY_ALGORITHM_ID_ENUM {
    NIA0, NIA1;
    public static INTEGRITY_ALGORITHM_ID_ENUM fromValue(int v) {
        switch (v) {
            case 0:
                return NIA0;
            case 1:
                return NIA1;
            default:
                throw new IllegalArgumentException("Invalid value for INTEGRITY_ALGORITHM_ID_ENUM: " + v);
        }
    }
}