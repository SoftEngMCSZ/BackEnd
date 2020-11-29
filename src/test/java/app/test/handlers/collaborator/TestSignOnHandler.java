package app.test.handlers.collaborator;

import me.whatdo.app.SignOnCollaboratorHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSignOnHandler {

    SignOnCollaboratorHandler handler;
    Alternative alt1, alt2;
    CreateChoiceRequest cRequest;
    Choice choice;
    ChoiceDAO choiceDAO;
    Collaborator collab;
    CollaboratorDAO collaboratorDAO;

    @Before
    public void setupHandler() throws Exception {

    }

    @Test
    public void successfulResponse() throws Exception {

    }
}
