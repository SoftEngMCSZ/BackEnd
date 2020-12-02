package app.test.model.request;

import me.whatdo.app.handlers.auth.UserAuthHandler;
import me.whatdo.app.model.request.ViewChoiceRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class ViewChoiceRequestTests {

	@Test
	public void allAttrConstructor() {
		UUID id = UUID.randomUUID();
		ViewChoiceRequest req = new ViewChoiceRequest(id.toString(), UserAuthHandler.encode("yeet:haw"));

		Assert.assertEquals(id.toString(),req.getID());
		Assert.assertEquals(UserAuthHandler.encode("yeet:haw"),req.getAuthentication());
	}

	@Test
	public void setters() {
		UUID id = UUID.randomUUID();
		ViewChoiceRequest req = new ViewChoiceRequest();
		req.setChoiceID(id.toString());
		req.setAuthentication(UserAuthHandler.encode("yeet:haw"));

		Assert.assertEquals(id.toString(),req.getID());
		Assert.assertEquals(UserAuthHandler.encode("yeet:haw"),req.getAuthentication());
	}

	@Test
	public void serialization() {
		ViewChoiceRequest req = new ViewChoiceRequest(UUID.randomUUID().toString(), UserAuthHandler.encode("yeet:haw"));

		Assert.assertEquals("{\"choiceID\":\""+req.getID()+"\",\"authentication\":\"eWVldDpoYXc=\"}",req.toJson());
	}
}
