package app.test.handlers.alternative;

import me.whatdo.app.OpinionHandler;
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

	private OpinionHandler handler;
	Alternative alt1;
	Alternative alt2;
	Collaborator approver;
	Collaborator disapprover;
	Choice choice;

	@Before
	public void init() throws Exception {
		handler = new OpinionHandler();
		alt1 = new Alternative("Option 1");
		alt2 = new Alternative("Option 2");
		approver = new Collaborator("I approve everything");
		disapprover = new Collaborator("I disapprove everything");
		choice = new Choice("Option 1 or Option 2?", Arrays.asList(alt1, alt2), 2);
		choice.addCollaborator(approver);
		choice.addCollaborator(disapprover);

		DatabaseUtil.connect().prepareStatement("TRUNCATE opinions;").execute();
		DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
		DatabaseUtil.connect().prepareStatement("TRUNCATE alternatives").execute();
		DatabaseUtil.connect().prepareStatement("TRUNCATE choices;").execute();

		new ChoiceDAO().addChoice(choice);
	}

	@Test
	public void successfulResponseAddRemoveApproval() {
		OpinionRequest req = new OpinionRequest(
				approver.getId(),
				alt1.getId(),
				choice.getId(),
				Opinion.APPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(approver));

		req = new OpinionRequest(
				approver.getId(),
				alt1.getId(),
				choice.getId(),
				Opinion.APPROVAL.toString(),
				"remove"
		);

		response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertFalse(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(approver));
	}

	@Test
	public void successfulResponseAddRemoveDisapproval() {
		OpinionRequest req = new OpinionRequest(
				disapprover.getId(),
				alt1.getId(),
				choice.getId(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getDisapprovals().contains(disapprover));
	}

	@Test
	public void failureResponseSameCollabApproveAndDisapprove() {
		OpinionRequest req = new OpinionRequest(
				approver.getId(),
				alt1.getId(),
				choice.getId(),
				Opinion.APPROVAL.toString(),
				"add"
		);

		ApiResponse response = handler.handleRequest(req, null);
		Assert.assertEquals(200, response.getStatusCode());

		Optional<Choice> maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertTrue(maybeNewState.isPresent());
		Assert.assertTrue(maybeNewState.get().getAlternatives().get(0).getApprovals().contains(approver));

		req = new OpinionRequest(
				approver.getId(),
				alt1.getId(),
				choice.getId(),
				Opinion.DISAPPROVAL.toString(),
				"add"
		);

		response = handler.handleRequest(req, null);
		Assert.assertEquals(400, response.getStatusCode());

		maybeNewState = Choice.fromJson(response.getBody());
		Assert.assertFalse(maybeNewState.isPresent());
	}

	@Test
	public void failureResponseInvalidChoiceId() {
		OpinionRequest req = new OpinionRequest(
				approver.getId(),
				alt1.getId(),
				UUID.randomUUID(),
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
				approver.getId(),
				UUID.randomUUID(),
				choice.getId(),
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
				UUID.randomUUID(),
				alt1.getId(),
				choice.getId(),
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
				approver.getId(),
				alt1.getId(),
				choice.getId(),
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
				approver.getId(),
				alt1.getId(),
				choice.getId(),
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
