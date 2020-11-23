package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class TestFeedback {

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    Collaborator collab = null;
    Feedback feedback = null;
    Alternative alt1;

    @Before
    public void setupTests() {
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("We could order dominos.");
        feedback = new Feedback(alt1.getID(), collab, Date.from(Instant.now()), "But I don't like pizza :(");
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(feedback);
    }

    @Test
    public void testSerialize(){
        JsonObject obj = feedback.toJsonObject();
        Assert.assertEquals(obj.get("feedbackID").toString(),gson.toJson(feedback.getID()));
        Assert.assertEquals(obj.get("author").toString(),gson.toJson(feedback.getAuthor()));
        Assert.assertEquals(obj.get("timestamp").toString(),gson.toJson(feedback.getTimestamp()));
        Assert.assertEquals(obj.get("contents").toString(), gson.toJson(feedback.getContent()));
    }

    @Test
    public void testDeserialize(){
        Feedback feedback2 = Feedback.fromJson(feedback.toJson());
        JsonObject obj = feedback.toJsonObject();
        JsonObject obj2 = feedback2.toJsonObject();
        Assert.assertEquals(obj.get("author").toString(),obj2.get("author").toString());
        Assert.assertEquals(obj.get("timestamp").toString(),obj2.get("timestamp").toString());
        Assert.assertEquals(obj.get("contents").toString(),obj2.get("contents").toString());

    }
}
