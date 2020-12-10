package app.test.handlers.alternative;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.whatdo.app.handlers.alternative.FeedbackHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.FeedbackRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class FeedbackHandlerTests {
	Alternative alt1;
	Alternative alt2;
	Collaborator collab1;
	Collaborator collab2;
	Choice choice;
	private FeedbackHandler handler;

	@Before
	public void init() throws Exception {
		handler = new FeedbackHandler();
		alt1 = new Alternative("Option 1");
		alt2 = new Alternative("Option 2");
		collab1 = new Collaborator("I give feedback");
		collab2 = new Collaborator("I don't");
		choice = new Choice("Option 1 or Option 2?", Arrays.asList(alt1, alt2), 2);
		choice.addCollaborator(collab1);
		choice.addCollaborator(collab2);

		DatabaseUtil.wipe();

		new ChoiceDAO().addChoice(choice);
	}

	@Test
	public void successfulResponse() {
		FeedbackRequest req = new FeedbackRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				"This is some Feedback"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> c = Choice.fromJson(response.getBody());
		Assert.assertTrue(c.isPresent());
		Choice output = c.get();
		Assert.assertEquals("This is some Feedback", output.getAlternatives().get(0).getFeedback().get(0).getContent());
	}

	@Test
	public void failureResponseMalformedRequest() {
		FeedbackRequest req = new FeedbackRequest(
				null,
				null,
				null,
				null
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 malformed FeedbackRequest", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMalformedId() {
		FeedbackRequest req = new FeedbackRequest(
				"This is not a UUID",
				"This is also not a UUID",
				"This is still not a UUID",
				"This doesn't have to be a UUID"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 ID malformed", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMissingCollaborator() {
		FeedbackRequest req = new FeedbackRequest(
				UUID.randomUUID().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				"This is some Feedback"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("404 Collaborator not found", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMissingAlternative() {
		FeedbackRequest req = new FeedbackRequest(
				collab1.getId().toString(),
				UUID.randomUUID().toString(),
				choice.getId().toString(),
				"This is some Feedback"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("404 Alternative not found", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMissingChoice() {
		FeedbackRequest req = new FeedbackRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				UUID.randomUUID().toString(),
				"This is some Feedback"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("404 Choice not found", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseAlreadyFinalized() throws Exception {
		new ChoiceDAO().finalizeChoice(choice.getId(), alt1.getId());
		FeedbackRequest req = new FeedbackRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				"This is some Feedback"
		);
		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 Choice already finalized", object.get("Message").getAsString());
	}
}

