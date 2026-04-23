public class RequestState {

    private Object arg;
    private RespondFn respond;

    public Object getArg() { return arg; }
    public void setArg(Object arg) { this.arg = arg; }

    public RespondFn getRespond() { return respond; }
    public void setRespond(RespondFn respond) { this.respond = respond; }
}
