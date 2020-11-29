package me.whatdo.app.handlers.collaborator;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class TestSignUpHandler {

    SignUpCollaboratorHandler handler;
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

        alt1 = new Alternative("Feed the fish");
        alt2 = new Alternative("Feed the giraffe");
        List<Alternative> alts = Arrays.asList(alt1, alt2);
        request = new ChoiceRequest("Which zoo animal do we feed?",alts,1);
        choice = new Choice(request);

        choiceDAO.addChoice(choice);
        handler = new SignUpCollaboratorHandler();
    }

    @Test
    public void successfulResponse() throws Exception {
        Map<String, String> pathParams, headers, queryParams;
        headers = new HashMap<>();
        pathParams = new HashMap<>();
        queryParams = new HashMap<>();
        headers.put("authentication", UserAuthHandler.encode("Max:pass"));
        pathParams.put("choiceID", choice.getId().toString());
        queryParams.put("username", "Max");
        queryParams.put("password", "pass");
        APIGatewayProxyRequestEvent event =
                new APIGatewayProxyRequestEvent()
                        .withHeaders(headers)
                        .withPathParameters(pathParams)
                        .withQueryStringParameters(queryParams)
                        .withHttpMethod("POST");


        APIGatewayProxyResponseEvent result = handler.handleRequest(event, null);

        String content = result.getBody();
        System.out.println(content);
        
        // FIXME: Not sure how to refactor this to use collaborator IDs.

//        assertEquals(result.getStatusCode().intValue(), 201);
//        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
//        assertNotNull(content);
//        assertTrue(content.contains("\"authentication\""));
//        assertTrue(content.contains(UserAuthHandler.encode("Max:pass")));
//        assertTrue(collaboratorDAO.deleteCollaborator(choice.getId(), new Collaborator("Max", "pass")));
    }
}
