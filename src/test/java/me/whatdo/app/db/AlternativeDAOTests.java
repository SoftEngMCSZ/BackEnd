package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Alternative;
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
		DatabaseUtil.connect().prepareStatement("TRUNCATE alternatives;").execute();
	}

	@Test
	public void addFetchDeleteSingle() throws Exception {
		UUID mockChoice = UUID.randomUUID();
		Alternative testAlt = new Alternative("Steal the Krabby Patty secret formula!");
		Assert.assertTrue(dao.addAlternative(mockChoice,testAlt));
		Optional<Alternative> testFetch = dao.getAlternative(testAlt.getId());
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(testAlt,testFetch.get());
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

		for(Alternative alt : alternatives) {
			Assert.assertTrue(dao.addAlternative(mockChoice,alt));
		}
		List<Alternative> out = dao.getAllAlternativesInChoice(mockChoice);
		Assert.assertEquals(alternatives.size(),out.size());

		for(Alternative alt : out) {
			Assert.assertTrue(dao.deleteAlternative(alt));
		}
	}

	@Test
	public void tryFetchMissing() throws Exception {
		UUID missingAltId = UUID.randomUUID();
		Assert.assertFalse(dao.getAlternative(missingAltId).isPresent());
	}
}
