package me.whatdo.app;

import me.whatdo.app.db.DatabaseUtil;
import me.whatdo.app.db.FeedbackDAO;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FeedbackDAOTest {
    private FeedbackDAO dao;

    @Before
    public void init() {
        this.dao = new FeedbackDAO();
    }

    @Test
    public void testAddFeedback() throws Exception {
        UUID alternativeID = UUID.randomUUID();
        Date timestamp = new Date();
        Collaborator author = new Collaborator("Vincent Vega");
        String content = "Come on, Mia. Let's go and get a steak.";

        dao.addFeedback(author, alternativeID, timestamp, content);

        List<Feedback> allFeedback = dao.getAllFeedback(alternativeID);
        Assert.assertEquals(1, allFeedback.size());
        Assert.assertEquals(content, allFeedback.get(0).getContent());
        Assert.assertEquals(author, allFeedback.get(0).getAuthor());
        Assert.assertEquals(timestamp, allFeedback.get(0).getTimestamp());
    }

    @Test
    public void testFeedbackSort() throws Exception {
        UUID alternativeID = UUID.randomUUID();
        Date timestamp = new Date(1954, 2, 14);
        Collaborator author = new Collaborator("Vincent Vega");
        String content = "Come on, Mia. Let's go and get a steak.";

        dao.addFeedback(author, alternativeID, timestamp, content);

        alternativeID = UUID.randomUUID();
        timestamp = new Date(1900, 7, 24);
        author = new Collaborator("Zelda Fitzgerald");
        content = "I need something stupid";


        dao.addFeedback(author, alternativeID, timestamp, content);

        List<Feedback> allFeedback = dao.getAllFeedback(alternativeID);
        Assert.assertEquals(author, allFeedback.get(0).getAuthor());

    }

    @Test
    public void testDeleteFeedback() throws Exception {
        UUID alternativeID = UUID.randomUUID();
        Date timestamp = new Date(1954, 2, 14);
        Collaborator author = new Collaborator("Vincent Vega");
        String content = "Come on, Mia. Let's go and get a steak.";

        dao.addFeedback(author, alternativeID, timestamp, content);

        dao.deleteFeedback(author, alternativeID, timestamp);
    }

}
