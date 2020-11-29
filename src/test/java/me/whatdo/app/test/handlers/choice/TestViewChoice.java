package me.whatdo.app.test.handlers.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import me.whatdo.app.ViewChoiceHandler;
import me.whatdo.app.test.db.ChoiceDAO;
import me.whatdo.app.test.db.CollaboratorDAO;
import me.whatdo.app.test.db.DatabaseUtil;
import me.whatdo.app.entitymodel.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestViewChoice {

    ViewChoiceHandler handler;
    Alternative alt1, alt2;
    CreateChoiceRequest cRequest;
    ViewChoiceRequest request;
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
        cRequest = new CreateChoiceRequest("Which zoo animal do we feed?",alts,1);
        choice = new Choice(cRequest);

        choiceDAO.addChoice(choice);

        handler = new ViewChoiceHandler();
        collaboratorDAO.addCollaborator(choice.getId(),collab);

    }

    @Test
    public void successfulResponse() {
        ApiResponse result = handler.handleRequest(request, null);

        assertEquals(result.getStatusCode(), 200);
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"alternatives\""));
        assertTrue(content.contains("\"collaborators\""));
    }

    @Test
    public void badHTTPMethod() {

    }

    @Test
    public void badRequest() {

    }

    @Test
    public void missingParam() {

    }

    @Test
    public void nonexstantID() {

    }
}
