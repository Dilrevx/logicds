package common;

public class EapMethodRet {
    public static final int DECISION_FAIL = 0;
    public static final int DECISION_UNCOND_SUCC = 1;

    public int decision = DECISION_FAIL;
    public int methodState = 0;
}
