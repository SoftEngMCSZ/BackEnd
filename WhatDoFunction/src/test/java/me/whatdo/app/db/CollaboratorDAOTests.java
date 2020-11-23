package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

public class CollaboratorDAOTests {
	CollaboratorDAO dao;

	@Before
	public void init() {
		this.dao = new CollaboratorDAO();
	}

	@Test // Note: currently broken due to DB constraints
	public void addFetchDelete() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Collaborator testCollab = new Collaborator("Bob the Builder","CanWeFixIt?");
		Assert.assertTrue(dao.addCollaborator(mockChoice,testCollab));
		Optional<Collaborator> testFetch = dao.getCollaborator(mockChoice,"Bob the Builder");
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testCollab.getName(),testFetch.get().getName());
		Assert.assertTrue(dao.deleteCollaborator(mockChoice,testCollab));
	}
}
