package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CollaboratorDAOTests {
	CollaboratorDAO dao;

	@Before
	public void init() throws Exception {
		this.dao = new CollaboratorDAO();
		DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators").execute();
	}

	@Test
	public void addFetchDeleteSingle() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Collaborator testCollab = new Collaborator("Bob the Builder","CanWeFixIt?");
		Assert.assertTrue(dao.addCollaborator(mockChoice,testCollab));
		Optional<Collaborator> testFetch = dao.getCollaborator(mockChoice,"Bob the Builder");
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testCollab.getName(),testFetch.get().getName());
		Assert.assertTrue(dao.deleteCollaborator(mockChoice,testCollab));
	}

	@Test
	public void addFetchDeleteBulk() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		List<Collaborator> collaborators = Arrays.asList(
				new Collaborator("Bob the Builder","YesWeCan!"),
				new Collaborator("Wendy"),
				new Collaborator("Bernard Bentley","I'mSoRichMyNameIsLiterallyBentley!!!"),
				new Collaborator("Leo")
		);

		for(Collaborator c : collaborators) {
			Assert.assertTrue(dao.addCollaborator(mockChoice,c));
		}
		List<Collaborator> out = dao.getAllCollaboratorsInChoice(mockChoice);
		Assert.assertEquals(collaborators.size(),out.size());

		for(Collaborator c : out) {
			Assert.assertTrue(dao.deleteCollaborator(mockChoice,c));
		}
	}
}
