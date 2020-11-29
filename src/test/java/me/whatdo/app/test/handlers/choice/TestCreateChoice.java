package me.whatdo.app.test.handlers.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.CreateChoiceHandler;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.ApiResponse;
import me.whatdo.app.entitymodel.CreateChoiceRequest;
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

        assertEquals(result.getStatusCode(), 201);
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"alternatives\""));
        assertTrue(content.contains("\"collaborators\""));
        System.out.println(content);
    }

    @Test
    public void badHTTPMethod() {
        ApiResponse result = handler.handleRequest(request, null);

        assertEquals(result.getStatusCode(), 405);
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"405 method not allowed\""));
    }

    @Test
    public void badRequest() {
        ApiResponse result = handler.handleRequest(request, null);

        assertEquals(result.getStatusCode(), 400);
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"400 malformed ChoiceRequest\""));
    }
}
