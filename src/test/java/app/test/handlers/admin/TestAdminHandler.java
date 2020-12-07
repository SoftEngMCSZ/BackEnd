package app.test.handlers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.whatdo.app.AdminReportHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.AdminRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestAdminHandler {

	AdminReportHandler handler;
	Choice newerChoice;
	Choice olderChoice;

	@Before
	public void init() throws Exception {
		handler = new AdminReportHandler();
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
				new Date(0, Calendar.JANUARY, 1),
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
				new Date(40, Calendar.JANUARY, 1),
				Optional.empty(),
				1
		);

		dao.addChoice(newerChoice);
		dao.addChoice(olderChoice);

	}

	@Test
	public void successfulResponseGetChoices() {
		AdminRequest req = new AdminRequest();
		ApiResponse response = handler.handleRequest(req, null);

		JsonObject object = new Gson().fromJson(response.getBody(), JsonObject.class);
		JsonArray choices = object.getAsJsonArray("choices").getAsJsonArray();
		Assert.assertEquals(olderChoice.getId(), UUID.fromString(choices.get(0).getAsJsonObject().get("id").getAsString()));
	}
}
