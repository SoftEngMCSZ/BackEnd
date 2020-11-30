package app.test.handlers.collaborator;

import me.whatdo.app.RegisterCollaboratorHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.CollaboratorRequest;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestRegisterHandler {

    RegisterCollaboratorHandler handler;
    CollaboratorRequest request, request2;
    ChoiceDAO choiceDAO;
    Choice choice;

    @Before
    public void setupHandler() throws Exception {
        DatabaseUtil.connect().prepareStatement("TRUNCATE opinions;").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE alternatives").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE choices;").execute();

        Alternative alt1, alt2;
        alt1 = new Alternative("Bloo");
        alt2 = new Alternative("Ornj");
        List<Alternative> alts = new ArrayList<>();
        alts.add(alt1);
        alts.add(alt2);
        choice = new Choice(new CreateChoiceRequest("What's my color", alts, 1));
        choiceDAO = new ChoiceDAO();
        choiceDAO.addChoice(choice);
        handler = new RegisterCollaboratorHandler();
    }

    @Test
    public void registerSecureRequest() throws Exception {
        request = new CollaboratorRequest(choice.getId().toString(),"Secure","Pass");
        ApiResponse response = handler.handleRequest(request,null);

        String content = response.getBody();
        Assert.assertNotNull(content);
        Assert.assertEquals(201, response.getStatusCode());

        ApiResponse response2 = handler.handleRequest(request,null);

        String content2 = response.getBody();
        Assert.assertNotNull(content2);
        Assert.assertEquals(200, response2.getStatusCode());
        Assert.assertEquals(content, content2);
    }

    @Test
    public void registerInsecureRequest() throws Exception {
        request = new CollaboratorRequest(choice.getId().toString(),"inSecure", "");
        ApiResponse response = handler.handleRequest(request,null);

        String content = response.getBody();
        Assert.assertNotNull(content);
        Assert.assertEquals(201, response.getStatusCode());

        ApiResponse response2 = handler.handleRequest(request,null);

        String content2 = response.getBody();
        Assert.assertNotNull(content2);
        Assert.assertEquals(200, response2.getStatusCode());
        Assert.assertEquals(content, content2);
    }

    @Test
    public void badUsernameRequest(){
        request = new CollaboratorRequest(choice.getId().toString(),"", "");
        ApiResponse response = handler.handleRequest(request,null);

        String content = response.getBody();
        Assert.assertNotNull(content);
        Assert.assertEquals(400, response.getStatusCode());
    }

    @Test
    public void badCredentialRequest(){
        request = new CollaboratorRequest(choice.getId().toString(),"Secure", "Pass");
        ApiResponse response = handler.handleRequest(request,null);

        String content = response.getBody();
        Assert.assertNotNull(content);
        Assert.assertEquals(201, response.getStatusCode());

        request2 = new CollaboratorRequest(choice.getId().toString(),"Secure", "");
        ApiResponse response2 = handler.handleRequest(request2,null);

        String content2 = response.getBody();
        Assert.assertNotNull(content2);
        Assert.assertEquals(401, response2.getStatusCode());
    }

    @Test
    public void maxCollobarotrRequest(){
        request = new CollaboratorRequest(choice.getId().toString(),"Secure", "Pass");
        ApiResponse response = handler.handleRequest(request,null);

        String content = response.getBody();
        Assert.assertNotNull(content);
        Assert.assertEquals(201, response.getStatusCode());

        request2 = new CollaboratorRequest(choice.getId().toString(),"Duplicate", "Pass");
        ApiResponse response2 = handler.handleRequest(request2,null);

        String content2 = response.getBody();
        Assert.assertNotNull(content2);
        Assert.assertEquals(400, response2.getStatusCode());
    }

    @Test
    public void badChoiceRequest(){
        request = new CollaboratorRequest("poggers","Secure", "Pass");
        ApiResponse result = handler.handleRequest(request,null);

        String content = result.getBody();
        assertNotNull(content);
        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void noChoiceRequest(){
        request = new CollaboratorRequest(UUID.randomUUID().toString(),"Secure", "Pass");
        ApiResponse result = handler.handleRequest(request,null);

        String content = result.getBody();
        assertNotNull(content);
        assertEquals(404, result.getStatusCode());
    }
}
