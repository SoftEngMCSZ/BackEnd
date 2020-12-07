package app.test.db;

import me.whatdo.app.db.AlternativeDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AlternativeDAOTests {
	AlternativeDAO dao;

	@Before
	public void init() throws Exception {
		this.dao = new AlternativeDAO();
		DatabaseUtil.wipe();
	}

	@Test
	public void addFetchDeleteSingle() throws Exception {
		CollaboratorDAO collabDao = new CollaboratorDAO();
		UUID mockChoice = UUID.randomUUID();
		Alternative testAlt = new Alternative("Steal the Krabby Patty secret formula!");
		Collaborator approver = new Collaborator("Mr. Krabs");
		Collaborator disapprover = new Collaborator("Squidward Tentacles");
		collabDao.addCollaborator(mockChoice, approver);
		collabDao.addCollaborator(mockChoice, disapprover);

		testAlt.addApproval(approver);
		testAlt.addDisapproval(disapprover);

		Assert.assertTrue(dao.addAlternative(mockChoice, testAlt));
		Optional<Alternative> testFetch = dao.getAlternative(testAlt.getId());
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testAlt, testFetch.get());
		Assert.assertTrue(dao.deleteAlternative(testAlt));
	}

	@Test
	public void addFetchDeleteBulk() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		List<Alternative> alternatives = Arrays.asList(
				new Alternative("Pretend to befriend SpongeBob to steal a Krabby Patty"),
				new Alternative("Dress up as a robot man and give SpongeBob $1,000,000 for the Krabby Patty formula"),
				new Alternative("Steal King Neptune's crown and frame Mr. Krabs"),
				new Alternative("Genuine self-introspection")
		);

		for (Alternative alt : alternatives) {
			Assert.assertTrue(dao.addAlternative(mockChoice, alt));
		}
		List<Alternative> out = dao.getAllAlternativesInChoice(mockChoice);
		Assert.assertEquals(alternatives.size(), out.size());

		Assert.assertEquals(alternatives.size(), dao.deleteAllAlternativesInChoice(mockChoice));
	}

	@Test
	public void tryFetchMissing() throws Exception {
		UUID missingAltId = UUID.randomUUID();
		Assert.assertFalse(dao.getAlternative(missingAltId).isPresent());
	}

	@Test
	public void doubleInsert() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Alternative alt = new Alternative("Become the God-Emperor of Bikini Bottom");
		Assert.assertTrue(dao.addAlternative(mockChoice, alt));
		Assert.assertFalse(dao.addAlternative(mockChoice, alt));
		Assert.assertTrue(dao.deleteAlternative(alt));
	}
}
