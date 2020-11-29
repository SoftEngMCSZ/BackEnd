package me.whatdo.app.test.db;

import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FeedbackDAOTests {
    private FeedbackDAO dao;

    Feedback testFeedback = new Feedback(
            UUID.randomUUID(),
            new Collaborator("Vincent Vega"),
            new Date(1954, 2, 14),
            "Come on, Mia. Let's go and get a steak."
    );

    @Before
    public void init() {
        this.dao = new FeedbackDAO();
    }

    @Test
    public void testAddFeedback() throws Exception {
        dao.addFeedback(this.testFeedback);

        List<Feedback> allFeedback = dao.getAllFeedback(testFeedback.getAlternativeID());
        Assert.assertEquals(1, allFeedback.size());
        Assert.assertEquals(testFeedback, allFeedback.get(0));
    }

    @Test
    public void testAddDupeFeedback() throws Exception {
        dao.addFeedback(testFeedback);

        Assert.assertFalse(dao.addFeedback(testFeedback));
    }

    @Test
    public void testFeedbackSort() throws Exception {
        dao.addFeedback(this.testFeedback);


        Feedback testFeedback2 = new Feedback(
                testFeedback.getAlternativeID(),
                new Collaborator("Zelda Fitzgerald"),
                new Date(1900, 7, 24),
                "Oh, God, goofo I'm drunk. Mark Twain. Isn't she smart—she has the hiccups. I hope it's beautiful and a fool—a beautiful little fool."
        );

        dao.addFeedback(testFeedback2);

        List<Feedback> allFeedback = dao.getAllFeedback(testFeedback.getAlternativeID());
        Assert.assertEquals(testFeedback2, allFeedback.get(0));
    }

    @Test
    public void testDeleteFeedback() throws Exception {
        dao.addFeedback(testFeedback);

        Assert.assertTrue(dao.deleteFeedback(testFeedback.getFeedbackID()));
        List<Feedback> allFeedback = dao.getAllFeedback(testFeedback.getAlternativeID());
        Assert.assertEquals(0, allFeedback.size());
    }

    @Test
    public void testGetFeedback() throws Exception {
        dao.addFeedback(testFeedback);

        Optional<Feedback> returnedFeedback = dao.getFeedback(testFeedback.getFeedbackID());
        Assert.assertTrue(returnedFeedback.isPresent());
        Assert.assertEquals(testFeedback, returnedFeedback.get());
    }

    @Test
    public void testGetMissingFeedback() throws Exception {
        Optional<Feedback> returnedFeedback = dao.getFeedback(testFeedback.getFeedbackID());
        Assert.assertFalse(returnedFeedback.isPresent());
    }
}
