package app.test.model.entity;

import com.google.gson.JsonObject;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestChoice {

	Alternative alt1, alt2 = null;
	Collaborator collab = null;
	CreateChoiceRequest request = null;
	Choice choice = null;

	@Before
	public void setupTests() {
		collab = new Collaborator("Maxy", "Baboo");
		alt1 = new Alternative("Pet fish");
		alt2 = new Alternative("Pet Dog");
		List<Alternative> alts = new ArrayList<Alternative>();
		alts.add(alt1);
		alts.add(alt2);
		request = new CreateChoiceRequest("What pet for the kids?", alts, 1);
		choice = new Choice(request);
	}

	@Test
	public void testConstructor() {
		Assert.assertNotNull(choice);
	}

	@Test
	public void testSerialize() {
		JsonObject obj = choice.toJsonObject();
		Assert.assertEquals(obj.get("question").toString(), "\"What pet for the kids?\"");
		Assert.assertEquals(obj.getAsJsonArray("alternatives").get(0).toString(), alt1.toJson());
		Assert.assertEquals(obj.get("maxCollaborators").getAsInt(), 1);
	}

	@Test
	public void testDeserialize() {
		choice.addCollaborator(collab);
		String jsonStr = choice.toJson();
		Choice choice2 = Choice.fromJson(jsonStr).get();
		Assert.assertTrue(choice2.hasCollaborator(collab));
	}

	@Test
	public void testCollaborator() {
		Assert.assertFalse(choice.hasCollaborator(collab));
		Assert.assertTrue(choice.addCollaborator(collab));
		Assert.assertTrue(choice.hasCollaborator(collab));
		Assert.assertFalse(choice.addCollaborator(collab));
	}

	@Test
	public void testFinalise() {
		Assert.assertFalse(choice.selectAlternative(new Alternative("No")));
		Assert.assertTrue(choice.selectAlternative(alt1));
	}


}
