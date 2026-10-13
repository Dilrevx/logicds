public class RequestState {

    private Object arg;
    private RespondFn respond;
    private KrbEncTktPart encTktReply;

    public Object getArg() { return arg; }
    public void setArg(Object arg) { this.arg = arg; }

    public RespondFn getRespond() { return respond; }
    public void setRespond(RespondFn respond) { this.respond = respond; }

    public KrbEncTktPart getEncTktReply() { return encTktReply; }
    public void setEncTktReply(KrbEncTktPart encTktReply) { this.encTktReply = encTktReply; }
}
