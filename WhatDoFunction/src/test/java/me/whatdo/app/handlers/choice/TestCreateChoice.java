package me.whatdo.app.handlers.choice;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.ChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestCreateChoice {

    CreateChoiceHandler handler;
    Alternative alt1, alt2;
    ChoiceRequest request;

    @Before
    public void setupHandler() {
        handler = new CreateChoiceHandler();
        alt1 = new Alternative("Feed the fish");
        alt2 = new Alternative("Feed the giraffe");
        List<Alternative> alts = Arrays.asList(alt1, alt2);
        request = new ChoiceRequest("Which zoo animal do we feed?", alts, 1);
    }

    @Test
    public void successfulResponse() {
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                .withBody(request.toJson())
                .withHttpMethod("POST");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 201);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"alternatives\""));
        assertTrue(content.contains("\"collaborators\""));
        System.out.println(content);
    }

    @Test
    public void badHTTPMethod() {
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withBody(request.toJson())
                        .withHttpMethod("GET");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 405);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"405 method not allowed\""));
    }

    @Test
    public void badRequest() {
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withBody("{}")
                        .withHttpMethod("POST");

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        assertEquals(result.getStatusCode().intValue(), 400);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"Message\":\"400 malformed ChoiceRequest\""));
    }
}
