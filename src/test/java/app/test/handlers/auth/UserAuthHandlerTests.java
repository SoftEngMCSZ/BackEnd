package app.test.handlers.auth;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class UserAuthHandlerTests {

	UUID mockChoiceId;
	Collaborator collab;

	@Before
	public void init() throws Exception {
		CollaboratorDAO dao = new CollaboratorDAO();
		DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
		this.mockChoiceId = UUID.randomUUID();
		this.collab = Collaborator.fromPlaintextPassword("SpongeBob SquarePants","GaryIsMyBestFriend");
		dao.addCollaborator(this.mockChoiceId,this.collab);
	}

	@Test
	public void successfulUserAuth() throws Exception {
		String mockAuthHeader = UserAuthHandler.encode("SpongeBob SquarePants:GaryIsMyBestFriend");
		Assert.assertTrue(UserAuthHandler.isUserAuthenticated(mockAuthHeader,mockChoiceId));
	}

	@Test
	public void failedAuthBadPassword() throws Exception {
		String mockAuthHeader = UserAuthHandler.encode("SpongeBob SquarePants:GaryIsMyWorstEnemy");
		Assert.assertFalse(UserAuthHandler.isUserAuthenticated(mockAuthHeader,mockChoiceId));
	}

	@Test
	public void failedAuthBadName() throws Exception {
		String mockAuthHeader = UserAuthHandler.encode("Patrick Starr:GaryIsMyBestFriend");
		Assert.assertFalse(UserAuthHandler.isUserAuthenticated(mockAuthHeader,mockChoiceId));
	}

	@Test
	public void failedAuthBadChoice() throws Exception {
		String mockAuthHeader = UserAuthHandler.encode("SpongeBob SquarePants:GaryIsMyBestFriend");
		Assert.assertFalse(UserAuthHandler.isUserAuthenticated(mockAuthHeader,UUID.randomUUID()));
	}
}
