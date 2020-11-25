package me.whatdo.app.handlers;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class UserAuthHandlerTests {
	UUID mockChoice = UUID.randomUUID();

	@Before
	public void init() throws Exception {
		DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
		new CollaboratorDAO().addCollaborator(mockChoice,new Collaborator("ISuckAtSecurity"));
	}

	@Test
	public void authPasswordlessUser() throws Exception {
		Assert.assertTrue(UserAuthHandler.isUserAuthenticated(
				UserAuthHandler.encode("ISuckAtSecurity:"),
				mockChoice
		));
	}
}
