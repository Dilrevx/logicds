package commons;

public class Helper {
    public static boolean testTimeIsPast = true;

    public static boolean mbedtls_x509_time_is_past(MbedtlsX509Time t) {
        return testTimeIsPast;
    }
}
