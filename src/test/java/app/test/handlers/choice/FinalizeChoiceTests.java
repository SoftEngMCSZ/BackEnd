package app.test.handlers.choice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.whatdo.app.FinalizeChoiceHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.FinalRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FinalizeChoiceTests {
	FinalizeChoiceHandler handler;
	Alternative alt1, alt2;
	Choice choice;
	ChoiceDAO choiceDAO;
	Collaborator collab;

	@Before
	public void init() throws Exception {
		DatabaseUtil.wipe();

		choiceDAO = new ChoiceDAO();
		collab = Collaborator.fromPlaintextPassword("Max", "pass");
		alt1 = new Alternative("Feed the fish");
		alt2 = new Alternative("Feed the giraffe");
		List<Alternative> alts = Arrays.asList(alt1, alt2);
		choice = new Choice("Which pet should starve?", alts, 2);
		choice.addCollaborator(collab);
		choiceDAO.addChoice(choice);
		handler = new FinalizeChoiceHandler();
	}

	@Test
	public void successfulResponse() {
		FinalRequest req = new FinalRequest(alt1.getId().toString(), choice.getId().toString());
		ApiResponse response = handler.handleRequest(req, null);

		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertTrue(object.has("finalAlternative"));
		Assert.assertEquals(alt1.getId().toString(), object.get("finalAlternative").getAsJsonObject().get("alternativeID").getAsString());
	}

	@Test
	public void failureResponseAlreadyFinalized() {
		FinalRequest req = new FinalRequest(alt1.getId().toString(), choice.getId().toString());
		ApiResponse response = handler.handleRequest(req, null);

		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertTrue(object.has("finalAlternative"));
		Assert.assertEquals(alt1.getId().toString(), object.get("finalAlternative").getAsJsonObject().get("alternativeID").getAsString());

		req = new FinalRequest(alt2.getId().toString(), choice.getId().toString());
		response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());
	}

	@Test
	public void failureResponseMissingChoice() {
		FinalRequest req = new FinalRequest(alt1.getId().toString(), UUID.randomUUID().toString());
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(404, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("404 Choice not found", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMissingAlternative() {
		FinalRequest req = new FinalRequest(UUID.randomUUID().toString(), choice.getId().toString());
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(404, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("404 Alternative not found", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMalformedId() {
		FinalRequest req = new FinalRequest("This is not a UUID", UUID.randomUUID().toString());
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 ID malformed", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseMalformedRequest() {
		FinalRequest req = new FinalRequest();
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 malformed FinalRequest", object.get("Message").getAsString());
	}
}
