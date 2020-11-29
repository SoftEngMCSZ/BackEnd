package app.test.handlers.collaborator;

import me.whatdo.app.SignUpCollaboratorHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CreateChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSignUpHandler {

    SignUpCollaboratorHandler handler;
    Alternative alt1, alt2;
    CreateChoiceRequest request;
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
