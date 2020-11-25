package me.whatdo.app.handlers.choice;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ChoiceRequest;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestViewChoice {

    ViewChoiceHandler handler;
    Alternative alt1, alt2;
    ChoiceRequest request;
    Choice choice;
    ChoiceDAO choiceDAO;
    Collaborator collab;
    CollaboratorDAO collaboratorDAO;

    @Before
    public void setupHandler() throws Exception {
        DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;TRUNCATE choices;").execute();
        choiceDAO = new ChoiceDAO();
        collaboratorDAO = new CollaboratorDAO();

        collab = Collaborator.fromPlaintextPassword("Max", "pass");
        alt1 = new Alternative("Feed the fish");
        alt2 = new Alternative("Feed the giraffe");
        List<Alternative> alts = Arrays.asList(alt1, alt2);
        request = new ChoiceRequest("Which zoo animal do we feed?",alts,1);
        choice = new Choice(request);

        choiceDAO.addChoice(choice);

        handler = new ViewChoiceHandler();
        collaboratorDAO.addCollaborator(choice.getId(),collab);

    }

    @Test
    public void successfulResponse() {
        Map<String, String> pathParams;
        Map<String, String > headers;
        pathParams = new HashMap<>();
        headers = new HashMap<>();
        pathParams.put("choiceID",choice.getId().toString());
        headers.put("Authentication", UserAuthHandler.encode("Max:pass"));
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withHeaders(headers)
                        .withPathParameters(pathParams)
                        .withHttpMethod("GET");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 200);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"alternatives\""));
        assertTrue(content.contains("\"collaborators\""));
    }

    @Test
    public void badHTTPMethod() {
        Map<String, String> pathParams;
        pathParams = new HashMap<>();
        pathParams.put("choiceID",choice.getId().toString());

        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withPathParameters(pathParams)
                        .withHttpMethod("POST");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 405);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"405 method not allowed\""));
    }

    @Test
    public void badRequest() {
        Map<String, String> pathParams;
        pathParams = new HashMap<>();
        pathParams.put("choiceID","malformed");

        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withPathParameters(pathParams)
                        .withHttpMethod("GET");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 400);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"400 malformed choiceID\""));
    }

    @Test
    public void missingParam() {
        Map<String, String> pathParams;
        pathParams = new HashMap<>();

        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withPathParameters(pathParams)
                        .withHttpMethod("GET");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 400);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"400 missing choiceID parameter\""));
    }

    @Test
    public void nonexstantID() {
        Map<String, String> pathParams;
        Map<String, String > headers;
        pathParams = new HashMap<>();
        headers = new HashMap<>();
        pathParams.put("choiceID",UUID.randomUUID().toString());
        headers.put("Authentication", UserAuthHandler.encode("Max:pass"));
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withHeaders(headers)
                        .withPathParameters(pathParams)
                        .withHttpMethod("GET");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 404);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"404 Choice not found\""));
    }
}
