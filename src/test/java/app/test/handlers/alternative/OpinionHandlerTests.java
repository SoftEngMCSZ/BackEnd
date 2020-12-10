package app.test.handlers.alternative;

import me.whatdo.app.handlers.alternative.OpinionHandler;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.db.Opinion;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.OpinionRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class OpinionHandlerTests {

	Alternative alt1;
	Alternative alt2;
	Collaborator collab1;
	Collaborator collab2;
	Choice choice;
	private OpinionHandler handler;

	@Before
	public void init() throws Exception {
		handler = new OpinionHandler();
		alt1 = new Alternative("Option 1");
		alt2 = new Alternative("Option 2");
		collab1 = new Collaborator("I approve everything");
		collab2 = new Collaborator("I disapprove everything");
		choice = new Choice("Option 1 or Option 2?", Arrays.asList(alt1, alt2), 2);
		choice.addCollaborator(collab1);
		choice.addCollaborator(collab2);

		DatabaseUtil.wipe();

		new ChoiceDAO().addChoice(choice);
	}

	@Test
	public void successfulResponseAddRemoveApproval() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.APPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(collab1));

		req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.APPROVAL.toString(),
				"remove"
		);

		response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertFalse(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(collab1));
	}

	@Test
	public void successfulResponseAddRemoveDisapproval() {
		OpinionRequest req = new OpinionRequest(
				collab2.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getDisapprovals().contains(collab2));
	}

	@Test
	public void successfulResponseSwitchOpinion() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.APPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(collab1));

		req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getDisapprovals().contains(collab1));
	}

	@Test
	public void failureResponseInvalidChoiceId() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				UUID.randomUUID().toString(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseInvalidAltId() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				UUID.randomUUID().toString(),
				choice.getId().toString(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseInvalidCollabId() {
		OpinionRequest req = new OpinionRequest(
				UUID.randomUUID().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(404, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseInvalidActionType() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				Opinion.DISAPPROVAL.toString(),
				"breakme"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseInvalidOpinionType() {
		OpinionRequest req = new OpinionRequest(
				collab1.getId().toString(),
				alt1.getId().toString(),
				choice.getId().toString(),
				"breakme",
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseIMissingData() {
		OpinionRequest req = new OpinionRequest(
				null,
				null,
				null,
				null,
				null
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}
}
