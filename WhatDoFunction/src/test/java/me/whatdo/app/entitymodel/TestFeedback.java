package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        gson = new Gson();
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

    }
}
