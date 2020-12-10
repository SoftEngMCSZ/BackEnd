package app.test.handlers.choice;

import me.whatdo.app.handlers.choice.CreateChoiceHandler;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestCreateChoice {

	CreateChoiceHandler handler;
	Alternative alt1, alt2;
	CreateChoiceRequest request;

	@Before
	public void setupHandler() throws Exception {
		DatabaseUtil.wipe();

		handler = new CreateChoiceHandler();
		alt1 = new Alternative("Feed the fish");
		alt2 = new Alternative("Feed the giraffe");
		List<Alternative> alts = Arrays.asList(alt1, alt2);
		request = new CreateChoiceRequest("Which zoo animal do we feed?", alts, 1);

	}

	@Test
	public void successfulResponse() {
		ApiResponse result = handler.handleRequest(request, null);

		assertEquals(200, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
		assertTrue(content.contains("\"id\""));
		assertTrue(content.contains("\"alternatives\""));
		assertTrue(content.contains("\"collaborators\""));
		Optional<Choice> choice = Choice.fromJson(content);
		assertTrue(choice.isPresent());
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
		ApiResponse result = handler.handleRequest(new CreateChoiceRequest(), null);

		assertEquals(400, result.getStatusCode());
		String content = result.getBody();
		assertNotNull(content);
	}
}
