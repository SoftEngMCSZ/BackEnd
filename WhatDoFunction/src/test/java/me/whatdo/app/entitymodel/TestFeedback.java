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

    Gson gsonLog, gson;
    Collaborator collab = null;
    Feedback feedback = null;

    @Before
    public void setupTests() {
        gson = new GsonBuilder().disableHtmlEscaping().create();
        gsonLog = new GsonBuilder().setPrettyPrinting().create();
        collab = new Collaborator("Maxy", "Baboo");
        feedback = new Feedback(collab, Date.from(Instant.now()), "But I don't like pizza :(");
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(feedback);
    }

    @Test
    public void testSerialize(){
        System.out.println(gsonLog.toJson(feedback));
        String jsonStr = gson.toJson(feedback);
        JsonObject obj = gson.fromJson(jsonStr, JsonObject.class);
        Assert.assertEquals(obj.get("author").toString(),gson.toJson(feedback.getAuthor()));
        Assert.assertEquals(obj.get("timestamp").toString(),gson.toJson(feedback.getTimestamp()));
        Assert.assertEquals(obj.get("content").toString(), gson.toJson(feedback.getContent()));
    }
}
