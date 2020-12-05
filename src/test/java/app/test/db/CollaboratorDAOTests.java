package app.test.db;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.entity.Collaborator;
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
		DatabaseUtil.wipe();
	}

	@Test
	public void addFetchDeleteSingleByName() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Collaborator testCollab = Collaborator.fromPlaintextPassword("Bob the Builder","CanWeFixIt?");
		Assert.assertTrue(dao.addCollaborator(mockChoice,testCollab));
		Optional<Collaborator> testFetch = dao.getCollaborator(mockChoice,"Bob the Builder");
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testCollab.getName(),testFetch.get().getName());
		Assert.assertTrue(testFetch.get().verifyPassword("CanWeFixIt?"));
		Assert.assertTrue(dao.deleteCollaborator(testFetch.get().getId()));
	}

	@Test
	public void addFetchDeleteSingleById() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Collaborator testCollab = Collaborator.fromPlaintextPassword("Bob the Builder","CanWeFixIt?");
		Assert.assertTrue(dao.addCollaborator(mockChoice,testCollab));
		Optional<Collaborator> testFetch = dao.getCollaborator(testCollab.getId());
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testCollab.getName(),testFetch.get().getName());
		Assert.assertTrue(testFetch.get().verifyPassword("CanWeFixIt?"));
		Assert.assertTrue(dao.deleteCollaborator(testFetch.get().getId()));
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
		Assert.assertEquals(collaborators,out);

		for(Collaborator c : out) {
			Assert.assertTrue(dao.deleteCollaborator(c.getId()));
		}
	}

	@Test
	public void getNonExistentCollaborator() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Assert.assertFalse(dao.getCollaborator(UUID.randomUUID()).isPresent());
	}

	@Test
	public void attemptDoubleInsert() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Collaborator testCollab = new Collaborator("Bob the Builder");
		Assert.assertTrue(dao.addCollaborator(mockChoice,testCollab));
		Assert.assertFalse(dao.addCollaborator(mockChoice,testCollab));
		Assert.assertTrue(dao.deleteCollaborator(testCollab.getId()));
	}

	@Test
	public void sameNameDifferentChoice() throws Exception {
		UUID mockChoice1 = UUID.randomUUID();
		UUID mockChoice2 = UUID.randomUUID();
		Collaborator testCollab = new Collaborator("Bob the Builder","CanWeFixIt?");
		Collaborator testCollab2 = new Collaborator("Bob the Builder","BuyANewOne!");
		Assert.assertTrue(dao.addCollaborator(mockChoice1,testCollab));
		Assert.assertTrue(dao.addCollaborator(mockChoice2,testCollab2));
	}
}
