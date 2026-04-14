package commons;

public class Helpers {
    public static int DecodeTls13SigAlg(byte[] input, Dcv13Args args) {
        args.sigAlgo = input[args.idx] & 0xFF;
        return 0;
    }
    public static int ato16(byte[] input, int idx) {
        return ((input[idx] & 0xFF) << 8) | (input[idx+1] & 0xFF);
    }
}
