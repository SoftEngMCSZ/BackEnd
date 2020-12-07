package app.test.db;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.db.Opinion;
import me.whatdo.app.db.OpinionDAO;
import me.whatdo.app.model.entity.Collaborator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OpinionDAOTests {
	private OpinionDAO dao;
	private UUID mockAltId;
	private UUID mockChoiceId;
	private Collaborator testOpinionAuthor = new Collaborator("Sandy Cheeks");

	@Before
	public void init() throws Exception {
		this.mockAltId = UUID.randomUUID();
		this.mockChoiceId = UUID.randomUUID();
		// The Feedback DAO asserts that the author is in the db, so we need a test one
		DatabaseUtil.wipe();
		new CollaboratorDAO().addCollaborator(this.mockChoiceId, testOpinionAuthor);
		this.dao = new OpinionDAO();
	}

	@Test
	public void addFetchDeleteSingle() throws Exception {
		dao.addOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL);

		Optional<Opinion> opinion = dao.getOpinion(mockAltId, testOpinionAuthor.getId());
		Assert.assertTrue(opinion.isPresent());
		Assert.assertEquals(Opinion.APPROVAL, opinion.get());
		Assert.assertTrue(dao.deleteOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL));
	}

	@Test
	public void addFetchDeleteBulk() throws Exception {
		Collaborator testOpinionAuthor2 = new Collaborator("Squidward Tentacles", "PleaseEndMe");
		new CollaboratorDAO().addCollaborator(this.mockChoiceId, testOpinionAuthor2);

		dao.addOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL);
		dao.addOpinion(mockAltId, testOpinionAuthor2.getId(), Opinion.APPROVAL);

		List<Collaborator> allApprovals = dao.getAllOpinionsForAlt(mockAltId, Opinion.APPROVAL);
		Assert.assertEquals(2, allApprovals.size());
		Assert.assertEquals(testOpinionAuthor, allApprovals.get(0));
		Assert.assertEquals(testOpinionAuthor2, allApprovals.get(1));
		Assert.assertEquals(2, dao.deleteAllOpinionsForAlt(mockAltId));
	}

	@Test
	public void testAddDupeOpinion() throws Exception {
		Assert.assertTrue(dao.addOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL));
		Assert.assertFalse(dao.addOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL));
		Assert.assertTrue(dao.deleteOpinion(mockAltId, testOpinionAuthor.getId(), Opinion.APPROVAL));
	}

	@Test
	public void testGetMissingOpinion() throws Exception {
		Optional<Opinion> returnedFeedback = dao.getOpinion(mockAltId, testOpinionAuthor.getId());
		Assert.assertFalse(returnedFeedback.isPresent());
	}
}
