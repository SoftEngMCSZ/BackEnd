package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ChoiceDAOTests {
	ChoiceDAO dao;

	@Before
	public void init() throws Exception {
		this.dao = new ChoiceDAO();
		DatabaseUtil.connect().prepareStatement("TRUNCATE choices;").execute();
	}

	@Test
	public void addFetchDeleteSingle() throws Exception {
		Choice mockChoice = new Choice(
				"How should we steal the Krabby Patty secret formula?",
				Arrays.asList(
						new Alternative("Break into Mr. Krabs' secret vault"),
						new Alternative("Befriend SpongeBob and get him to give it to us."),
						new Alternative("Give up and make a better competing product.")
				),
				3
		);
		mockChoice.addCollaborator(new Collaborator("Plankton","I command you!"));
		mockChoice.addCollaborator(new Collaborator("Karen"));
		Assert.assertTrue(dao.addChoice(mockChoice));
		Optional<Choice> testFetch = dao.getChoice(mockChoice.getId());
		Assert.assertTrue(testFetch.isPresent());
		Assert.assertEquals(mockChoice,testFetch.get());
		Assert.assertEquals(mockChoice.getAlternatives(),testFetch.get().getAlternatives());
		Assert.assertEquals(mockChoice.getCollaborators(),testFetch.get().getCollaborators());
		Assert.assertTrue(dao.deleteChoice(mockChoice));
	}

	@Test
	public void addFetchDeleteBulk() throws Exception {
		List<Choice> choices = Arrays.asList(
				new Choice(
						"How should we steal the Krabby Patty secret formula?",
						Arrays.asList(
								new Alternative("Break into Mr. Krabs' secret vault"),
								new Alternative("Befriend SpongeBob and get him to give it to us."),
								new Alternative("Give up and make a better competing product.")
						),
						3
				),
				new Choice(
						"How should I continue living my capitalist fantasy as the proprietor of the Krusty Krab?",
						Arrays.asList(
								new Alternative("Lord my secret formula over Plankton"),
								new Alternative("Underpay and overwork Spongebob and Squidward"),
								new Alternative("Expand my empire by building a new Krusty Krab right next to the first one")
						),
						3
				)
		);

		choices.get(0).addCollaborator(new Collaborator("Plankton","KaReNtHeLoVeOfMyLiFe!!1!"));
		choices.get(0).addCollaborator(new Collaborator("Karen"));

		choices.get(1).addCollaborator(new Collaborator("Mr. Krabs","IWantMoney$$$"));
		choices.get(1).addCollaborator(new Collaborator("King Neptune"));

		for(Choice c : choices) {
			Assert.assertTrue(dao.addChoice(c));
		}
		List<Choice> out = dao.getAllChoices();
		Assert.assertEquals(choices,out);

		for(Choice c : out) {
			Assert.assertTrue(dao.deleteChoice(c));
		}
	}

	@Test
	public void tryFetchMissing() throws Exception {
		UUID missingChoiceId = UUID.randomUUID();
		Assert.assertFalse(dao.getChoice(missingChoiceId).isPresent());
	}

	@Test
	public void attemptDoubleInsert() throws Exception {
		Choice mockChoice = new Choice(
				"How should we steal the Krabby Patty secret formula?",
				Arrays.asList(
						new Alternative("Break into Mr. Krabs' secret vault"),
						new Alternative("Befriend SpongeBob and get him to give it to us."),
						new Alternative("Give up and make a better competing product.")
				),
				3
		);

		Assert.assertTrue(dao.addChoice(mockChoice));
		Assert.assertFalse(dao.addChoice(mockChoice));
		Assert.assertTrue(dao.deleteChoice(mockChoice));
	}

	@Test
	public void finalizeChoice() throws Exception {
		Alternative finalAlt = new Alternative("Go into the furniture business");

		Choice mockChoice = new Choice(
				"How should I make a profit?",
				Arrays.asList(
						finalAlt,
						new Alternative("Steal the Krabby Patty secret formula")
				),
				2
		);

		Assert.assertTrue(dao.addChoice(mockChoice));
		Assert.assertTrue(dao.finalizeChoice(mockChoice.getId(),finalAlt.getId()));
		Assert.assertFalse(dao.finalizeChoice(mockChoice.getId(),mockChoice.getAlternatives().get(1).getId()));
		Assert.assertEquals(Optional.of(Optional.of(finalAlt)),
				dao.getChoice(mockChoice.getId()).map(Choice::getSelectedAlternative));
	}

	@Test
	public void finalizeMissingChoice() throws Exception {
		Assert.assertFalse(dao.finalizeChoice(UUID.randomUUID(),UUID.randomUUID()));
	}

	@Test
	public void deleteByDate() throws Exception {
		Choice newerChoice = new Choice(
				"How should I do the thing?",
				Arrays.asList(
						new Alternative("Thing A"),
						new Alternative("Thing B")
				),
				1
		);
		Choice olderChoice = new Choice(
				UUID.randomUUID(),
				"How should I do the other thing?",
				Arrays.asList(
						new Alternative("Thing C"),
						new Alternative("Thing D")
				),
				new HashSet<>(),
				Optional.empty(),
				new Date(0, Calendar.JANUARY, 1),
				Optional.empty(),
				1
		);

		Assert.assertTrue(dao.addChoice(olderChoice));
		Assert.assertTrue(dao.addChoice(newerChoice));

		Assert.assertEquals(1,dao.deleteChoicesOlderThan(new Date(0, Calendar.JANUARY, 2)));
		Assert.assertEquals(1,dao.getAllChoices().size());
	}
}
