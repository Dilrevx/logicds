public class ScpContext {

    private final FileSystem fs;
    private boolean pflag = true;
    private boolean iamrecursive = true;
    private boolean iamremote = false;
    private boolean targetshouldbedirectory = false;
    private boolean verboseMode = false;
    private int errs = 0;
    private int umask = 022;

    public ScpContext(FileSystem fs) {
        this.fs = fs;
    }

    public FileSystem getFs() { return fs; }

    public boolean isPflag() { return pflag; }
    public void setPflag(boolean pflag) { this.pflag = pflag; }

    public boolean isIamrecursive() { return iamrecursive; }
    public void setIamrecursive(boolean iamrecursive) { this.iamrecursive = iamrecursive; }

    public boolean isIamremote() { return iamremote; }
    public void setIamremote(boolean iamremote) { this.iamremote = iamremote; }

    public boolean isTargetshouldbedirectory() { return targetshouldbedirectory; }
    public void setTargetshouldbedirectory(boolean v) { this.targetshouldbedirectory = v; }

    public boolean isVerboseMode() { return verboseMode; }
    public void setVerboseMode(boolean v) { this.verboseMode = v; }

    public int getErrs() { return errs; }
    public void incErrs() { this.errs++; }

    public int getUmask() { return umask; }
    public void setUmask(int umask) { this.umask = umask; }
}
