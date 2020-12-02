package app.test.model.request;

import me.whatdo.app.model.request.CollaboratorRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class CollaboratorRequestTests {

	@Test
	public void allAttrsConstructor() {
		UUID id = UUID.randomUUID();
		CollaboratorRequest req = new CollaboratorRequest(id.toString(),"yeet","haw");

		Assert.assertEquals(id.toString(),req.getChoiceID());
		Assert.assertEquals("yeet",req.getUsername());
		Assert.assertEquals("haw",req.getPassword());
	}

	@Test
	public void setters() {
		UUID id = UUID.randomUUID();
		CollaboratorRequest req = new CollaboratorRequest();
		req.setChoiceID(id.toString());
		req.setUsername("yeet");
		req.setPassword("haw");

		Assert.assertEquals(id.toString(),req.getChoiceID());
		Assert.assertEquals("yeet",req.getUsername());
		Assert.assertEquals("haw",req.getPassword());
	}
}
