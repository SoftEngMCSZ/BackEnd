package app.test.handlers.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.CreateChoiceHandler;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestCreateChoice {

    CreateChoiceHandler handler;
    Alternative alt1, alt2;
    CreateChoiceRequest request;

    @Before
    public void setupHandler() throws Exception {
        handler = new CreateChoiceHandler();
        alt1 = new Alternative("Feed the fish");
        alt2 = new Alternative("Feed the giraffe");
        List<Alternative> alts = Arrays.asList(alt1, alt2);
        request = new CreateChoiceRequest("Which zoo animal do we feed?", alts, 1);
        DatabaseUtil.connect().prepareStatement("TRUNCATE choices;").execute();
    }

    @Test
    public void successfulResponse() {
        ApiResponse result = handler.handleRequest(request, null);

        assertEquals(200, result.getStatusCode());
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"alternatives\""));
        assertTrue(content.contains("\"collaborators\""));
        System.out.println(content);
    }

    @Test
    public void badRequest() {
        ApiResponse result = handler.handleRequest(null, null);

        assertEquals(500, result.getStatusCode() );
        String content = result.getBody();
        assertNotNull(content);
    }
}
