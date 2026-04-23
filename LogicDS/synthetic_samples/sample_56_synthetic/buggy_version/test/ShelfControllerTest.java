import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ShelfControllerTest {

    private User user;
    private ShelfController controller;
    private Shelf shelf;

    @Before
    public void setUp() {
        Session.reset();
        Config.reset();

        user = new User();
        user.setId(7);
        controller = new ShelfController(user);
        shelf = new Shelf();
    }

    private Request postForm(Map<String, String> form) {
        Request r = new Request();
        r.setMethod("POST");
        r.form = form;
        return r;
    }

    @Test
    public void testAuthorizedUserCanCreatePublicShelf() {
        user.setCanEditShelfs(true);

        Map<String, String> form = new HashMap<String, String>();
        form.put("title", "MyShelf");
        form.put("is_public", "on");
        Request req = postForm(form);

        ShelfResponse response = controller.createEditShelf(shelf, "Edit", 0, null, req);

        assertEquals("authorized public shelf should be created (isPublic=1)", 1, shelf.getIsPublic());
        assertEquals("expect redirect on success", ShelfResponse.Kind.REDIRECT, response.kind);
    }

    @Test
    public void testNonOnValueDoesNotMarkPublic() {
        user.setCanEditShelfs(false);

        Map<String, String> form = new HashMap<String, String>();
        form.put("title", "SneakyShelf");
        form.put("is_public", "true");
        Request req = postForm(form);

        controller.createEditShelf(shelf, "Edit", 0, null, req);

        assertEquals("unauthorized user sending non-\"on\" truthy value must not create a public shelf",
                0, shelf.getIsPublic());
    }
}
