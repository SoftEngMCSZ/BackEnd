package app.test.model.request;

import me.whatdo.app.model.request.OpinionRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class OpinionRequestTests {
	@Test
	public void allAttrsConstructor() {
		UUID collabId = UUID.randomUUID();
		UUID altId = UUID.randomUUID();
		UUID choiceId = UUID.randomUUID();
		OpinionRequest req = new OpinionRequest(collabId.toString(), altId.toString(),
				choiceId.toString(), "APPROVAL", "add");

		Assert.assertEquals(collabId.toString(), req.getCollabId());
		Assert.assertEquals(altId.toString(), req.getAlternativeId());
		Assert.assertEquals(choiceId.toString(), req.getChoiceId());
		Assert.assertEquals("APPROVAL", req.getOpinionType());
		Assert.assertEquals("add", req.getActionType());
	}

	@Test
	public void setters() {
		UUID collabId = UUID.randomUUID();
		UUID altId = UUID.randomUUID();
		UUID choiceId = UUID.randomUUID();
		OpinionRequest req = new OpinionRequest();

		req.setCollabId(collabId.toString());
		req.setAlternativeId(altId.toString());
		req.setChoiceId(choiceId.toString());
		req.setOpinionType("APPROVAL");
		req.setActionType("add");

		Assert.assertEquals(collabId.toString(), req.getCollabId());
		Assert.assertEquals(altId.toString(), req.getAlternativeId());
		Assert.assertEquals(choiceId.toString(), req.getChoiceId());
		Assert.assertEquals("APPROVAL", req.getOpinionType());
		Assert.assertEquals("add", req.getActionType());
	}

	@Test
	public void serialization() {
		UUID collabId = UUID.randomUUID();
		UUID altId = UUID.randomUUID();
		UUID choiceId = UUID.randomUUID();
		OpinionRequest req = new OpinionRequest(collabId.toString(), altId.toString(),
				choiceId.toString(), "APPROVAL", "add");

		OpinionRequest req2 = OpinionRequest.fromJson(req.toJson());

		Assert.assertEquals(req.getCollabId(), req2.getCollabId());
		Assert.assertEquals(req.getAlternativeId(), req2.getAlternativeId());
		Assert.assertEquals(req.getChoiceId(), req2.getChoiceId());
		Assert.assertEquals(req.getOpinionType(), req2.getOpinionType());
		Assert.assertEquals(req.getActionType(), req2.getActionType());
	}
}
