public class ShelfResponse {

    public enum Kind { REDIRECT, TEMPLATE }

    public final Kind kind;
    public final String target;
    public final String flashMessage;
    public final String flashCategory;

    public ShelfResponse(Kind kind, String target, String flashMessage, String flashCategory) {
        this.kind = kind;
        this.target = target;
        this.flashMessage = flashMessage;
        this.flashCategory = flashCategory;
    }

    public static ShelfResponse redirect(String target, String flashMessage, String flashCategory) {
        return new ShelfResponse(Kind.REDIRECT, target, flashMessage, flashCategory);
    }

    public static ShelfResponse template(String target) {
        return new ShelfResponse(Kind.TEMPLATE, target, null, null);
    }
}
