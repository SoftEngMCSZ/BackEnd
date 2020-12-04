package app.test.handlers.admin;

import me.whatdo.app.AdminReportHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.AdminRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TestAdminHandler {


    AdminReportHandler handler;
    Alternative alt1;
    Alternative alt2;
    Alternative alt3;
    Alternative alt4;
    Collaborator collaborator1;
    Collaborator collaborator2;
    Collaborator collaborator3;
    Collaborator collaborator4;
    Choice choice;

    @Before
    public void init() throws Exception {
        handler = new AdminReportHandler();
        ChoiceDAO dao = new ChoiceDAO();


        alt1 = new Alternative("Option 1");
        alt2 = new Alternative("Option 2");
        collaborator1 = new Collaborator("I approve everything");
        collaborator2 = new Collaborator("I disapprove everything");
        choice = new Choice("Option 1 or Option 2?", Arrays.asList(alt1, alt2), 2);
        choice.addCollaborator(collaborator1);
        choice.addCollaborator(collaborator2);

        dao.addChoice(choice);

        alt3 = new Alternative("Hawaiian Pizza");
        alt4 = new Alternative("I disapprove everything");
        collaborator3 = new Collaborator("Andy");
        collaborator4 = new Collaborator("Chantal");
        choice = new Choice("Second choice", Arrays.asList(alt3, alt4), 4);
        choice.addCollaborator(collaborator3);
        choice.addCollaborator(collaborator4);

        dao.addChoice(choice);

        DatabaseUtil.connect().prepareStatement("TRUNCATE opinions;").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE alternatives").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE choices;").execute();



    }

    @Test
    public void successfulResponseGetChoices() {
        AdminRequest req = new AdminRequest();
        ApiResponse response = handler.handleRequest(req, null);
    }
}
