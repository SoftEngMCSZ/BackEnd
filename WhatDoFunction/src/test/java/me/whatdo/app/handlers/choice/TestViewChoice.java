package me.whatdo.app.handlers.choice;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestViewChoice {

    ViewChoiceHandler handler;
    Alternative alt1, alt2;
    ChoiceRequest request;
    Choice choice;
    ChoiceDAO dao;

    @Before
    public void setupHandler() throws Exception {
        handler = new ViewChoiceHandler();

        alt1 = new Alternative("Feed the fish");
        alt2 = new Alternative("Feed the giraffe");
        List<Alternative> alts = Arrays.asList(alt1, alt2);
        request = new ChoiceRequest("Which zoo animal do we feed?",alts,1);
        choice = new Choice(request);

        dao = new ChoiceDAO();
        dao.addChoice(choice);
    }

    @Test
    public void successfulResponse() {
        Map<String, String> pathParams;
        pathParams = new HashMap<>();
        pathParams.put("choiceID",choice.getId().toString());

        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
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
}
