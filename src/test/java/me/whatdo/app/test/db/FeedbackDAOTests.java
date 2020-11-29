package me.whatdo.app.test.db;

import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

public class FeedbackDAOTests {
    private FeedbackDAO dao;
    private final Collaborator testFeedbackAuthor = new Collaborator("Vincent Vega");
    private UUID mockChoiceId;
    private UUID mockAltId;

    private Feedback testFeedback;

    @Before
    public void init() throws Exception {
        this.mockChoiceId = UUID.randomUUID();
        this.mockAltId = UUID.randomUUID();
        DatabaseUtil.connect().prepareStatement("TRUNCATE feedback;").execute();
        DatabaseUtil.connect().prepareStatement("TRUNCATE collaborators;").execute();
        // The Feedback DAO asserts that the author is in the db, so we need a test one
        new CollaboratorDAO().addCollaborator(mockChoiceId,testFeedbackAuthor);
        this.dao = new FeedbackDAO();

        Calendar cal = new GregorianCalendar();
        cal.set(1954, Calendar.FEBRUARY, 14);

        this.testFeedback = new Feedback(
                testFeedbackAuthor,
                cal.getTime(),
                "Come on, Mia. Let's go and get a steak."
        );
    }

    @Test
    public void testAddFeedback() throws Exception {
        dao.addFeedback(mockAltId,this.testFeedback);

        List<Feedback> allFeedback = dao.getAllFeedback(mockAltId);
        Assert.assertEquals(1, allFeedback.size());
        Assert.assertEquals(testFeedback, allFeedback.get(0));
    }

    @Test
    public void addFetchDeleteBulk() throws Exception {
        UUID mockAltId = UUID.randomUUID();
        CollaboratorDAO collabDao = new CollaboratorDAO();
        for (Collaborator collaborator : Arrays.asList(
                new Collaborator("Alice"),
                new Collaborator("Bob"),
                new Collaborator("Charlie"),
                new Collaborator("Denise")
        )) {
            collabDao.addCollaborator(mockAltId, collaborator);
            dao.addFeedback(mockAltId,new Feedback(collaborator,Date.from(Instant.now()),"No."));
        }

        Assert.assertEquals(4,dao.deleteAllFeedbackForAlternative(mockAltId));
    }

    @Test
    public void testAddDupeFeedback() throws Exception {
        dao.addFeedback(mockAltId,testFeedback);

        Assert.assertFalse(dao.addFeedback(mockAltId,testFeedback));
    }

    @Test
    public void testFeedbackSort() throws Exception {
        dao.addFeedback(mockAltId,this.testFeedback);
        Collaborator testCollab = new Collaborator("Zelda Fitzgerald");
        new CollaboratorDAO().addCollaborator(mockChoiceId,testCollab);

        Calendar cal = new GregorianCalendar();
        cal.set(1900,Calendar.AUGUST,24);

        Feedback testFeedback2 = new Feedback(
                testCollab,
                cal.getTime(),
                "Oh, God, goofo I'm drunk. Mark Twain. Isn't she smart—she has the hiccups. I hope it's beautiful and a fool—a beautiful little fool."
        );

        dao.addFeedback(mockAltId,testFeedback2);

        List<Feedback> allFeedback = dao.getAllFeedback(mockAltId);
        Assert.assertEquals(testFeedback2, allFeedback.get(0));
    }

    @Test
    public void testDeleteFeedback() throws Exception {
        dao.addFeedback(mockAltId,testFeedback);

        Assert.assertTrue(dao.deleteFeedback(testFeedback.getId()));
        List<Feedback> allFeedback = dao.getAllFeedback(mockAltId);
        Assert.assertEquals(0, allFeedback.size());
    }

    @Test
    public void testGetFeedback() throws Exception {
        dao.addFeedback(mockAltId,testFeedback);

        Optional<Feedback> returnedFeedback = dao.getFeedback(testFeedback.getId());
        Assert.assertTrue(returnedFeedback.isPresent());
        Assert.assertEquals(testFeedback, returnedFeedback.get());
    }

    @Test
    public void testGetMissingFeedback() throws Exception {
        Optional<Feedback> returnedFeedback = dao.getFeedback(testFeedback.getId());
        Assert.assertFalse(returnedFeedback.isPresent());
    }
}
