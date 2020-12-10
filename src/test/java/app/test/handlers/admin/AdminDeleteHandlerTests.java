package app.test.handlers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.whatdo.app.handlers.admin.AdminDeleteHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.AdminRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

public class AdminDeleteHandlerTests {

	AdminDeleteHandler handler;
	Choice newerChoice;
	Choice olderChoice;

	@Before
	public void init() throws Exception {
		handler = new AdminDeleteHandler();
		DatabaseUtil.wipe();
		ChoiceDAO dao = new ChoiceDAO();

		newerChoice = new Choice(
				UUID.randomUUID(),
				"First choice",
				Arrays.asList(
						new Alternative("Thing C"),
						new Alternative("Thing D")
				),
				new HashSet<>(),
				Optional.empty(),
				Date.from(Instant.now().minusSeconds(24 * 60 * 60)),
				Optional.empty(),
				1
		);
		olderChoice = new Choice(
				UUID.randomUUID(),
				"Second choice",
				Arrays.asList(
						new Alternative("Thing C"),
						new Alternative("Thing D")
				),
				new HashSet<>(),
				Optional.empty(),
				Date.from(Instant.now().minusSeconds(2 * 24 * 60 * 60)),
				Optional.empty(),
				1
		);

		dao.addChoice(newerChoice);
		dao.addChoice(olderChoice);
	}

	@Test
	public void successfulResponseDeleteOneChoice() {
		AdminRequest req = new AdminRequest(1.5);
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(200, response.getStatusCode());

		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		JsonArray choices = object.getAsJsonArray("choices").getAsJsonArray();

		Assert.assertEquals(1, choices.size());
		Assert.assertEquals(newerChoice.getId(), UUID.fromString(choices.get(0).getAsJsonObject().get("id").getAsString()));
	}

	@Test
	public void successfulResponseDeleteTwoChoices() {
		AdminRequest req = new AdminRequest(0.25);
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(200, response.getStatusCode());

		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		JsonArray choices = object.getAsJsonArray("choices").getAsJsonArray();

		Assert.assertEquals(0, choices.size());
	}

	@Test
	public void failureResponseMalformedRequest() {
		AdminRequest req = new AdminRequest();
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertEquals("400 malformed AdminRequest", object.get("Message").getAsString());
	}

	@Test
	public void failureResponseNoChoicesOldEnough() {
		AdminRequest req = new AdminRequest(3);
		ApiResponse response = handler.handleRequest(req, null);

		Assert.assertEquals(400, response.getStatusCode());
		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		Assert.assertTrue(object.has("Message"));
		Assert.assertNotEquals("400 malformed AdminRequest", object.get("Message").getAsString());
	}
}
