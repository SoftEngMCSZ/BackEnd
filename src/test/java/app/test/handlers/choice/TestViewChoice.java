package app.test.handlers.choice;

import me.whatdo.app.handlers.choice.ViewChoiceHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.CreateChoiceRequest;
import me.whatdo.app.model.request.ViewChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

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
		DatabaseUtil.wipe();

		choiceDAO = new ChoiceDAO();
		collaboratorDAO = new CollaboratorDAO();
		collab = Collaborator.fromPlaintextPassword("Max", "pass");
		alt1 = new Alternative("Feed the fish");
		alt2 = new Alternative("Feed the giraffe");
		List<Alternative> alts = Arrays.asList(alt1, alt2);
		cRequest = new CreateChoiceRequest("Which zoo animal do we feed?", alts, 1);
		choice = new Choice(cRequest);
		choiceDAO.addChoice(choice);
		handler = new ViewChoiceHandler();
		collaboratorDAO.addCollaborator(choice.getId(), collab);
		request = new ViewChoiceRequest();

	}

	@Test
	public void successfulRequest() {
		request.setChoiceID(choice.getId().toString());
		request.setAuthentication(UserAuthHandler.encode("Max:pass"));
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(200, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
		assertTrue(content.contains("\"id\""));
		assertTrue(content.contains("\"alternatives\""));
		assertTrue(content.contains("\"collaborators\""));
	}

	@Test
	public void nullRequest() {
		ApiResponse result = handler.handleRequest(null, null);

		assertEquals(500, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

	@Test
	public void emptyRequest() {
		ApiResponse result = handler.handleRequest(new ViewChoiceRequest(), null);

		assertEquals(400, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

	@Test
	public void badAuthRequest() {
		request.setChoiceID(choice.getId().toString());
		request.setAuthentication(UserAuthHandler.encode("Max:@@@"));
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(401, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

	@Test
	public void noAuthRequest() {
		request.setChoiceID(choice.getId().toString());
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(400, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

	@Test
	public void badChoiceRequest() {
		request.setChoiceID("poggers");
		request.setAuthentication(UserAuthHandler.encode("Max:pass"));
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(400, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

	@Test
	public void noChoiceRequest() {
		request.setChoiceID(UUID.randomUUID().toString());
		request.setAuthentication(UserAuthHandler.encode("Max:pass"));
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(404, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}

}
