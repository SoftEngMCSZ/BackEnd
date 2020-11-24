package me.whatdo.app.handlers.auth;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.entitymodel.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;
import java.util.UUID;

public class UserAuthHandlerTests {

	UUID mockChoiceId;
	Collaborator collab;

	@Before
	public void init() throws Exception {
		CollaboratorDAO dao = new CollaboratorDAO();
		DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
		this.mockChoiceId = UUID.randomUUID();
		this.collab = new Collaborator("SpongeBob SquarePants","GaryIsMyBestFriend");
		dao.addCollaborator(this.mockChoiceId,this.collab);
	}

	@Test
	public void successfulUserAuth() throws Exception {
		Base64.Encoder encoder = Base64.getEncoder();
		String mockAuthHeader = encoder.encodeToString("SpongeBob SquarePants:GaryIsMyBestFriend2".getBytes());
		Assert.assertTrue(UserAuthHandler.isUserAuthenticated(mockAuthHeader,mockChoiceId));
	}

}
