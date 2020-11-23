package me.whatdo.app.entitymodel;

import com.google.gson.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class TestAlternative {
    Gson gsonLog, gson;
    Collaborator collab = null;
    Alternative alt1,alt2 = null;
    Feedback feedback = null;



    @Before
    public void setupTests() {
        gson = new GsonBuilder().disableHtmlEscaping().create();
        gsonLog = new GsonBuilder().setPrettyPrinting().create();
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("We eat pizza?");
        feedback = new Feedback(collab, Date.from(Instant.now()), "But I don't like pizza :(");
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(alt1);
        Assert.assertEquals(alt1.getDescription(), "We eat pizza?");
    }

    @Test
    public void testSerialize(){
        String str = gson.toJson(alt1);
        JsonObject obj = gson.fromJson(str,JsonObject.class);
        Assert.assertEquals(obj.get("description").toString(), "\"We eat pizza?\"");
    }

    @Test
    public void testDeserialize(){
        String jsonStr = gson.toJson(alt1);
        alt2 = gson.fromJson(jsonStr,Alternative.class);
        Assert.assertNotNull(alt2);
        Assert.assertEquals(alt1.hashCode(), alt2.hashCode());
    }

    @Test
    public void testApproval(){
        Assert.assertFalse(alt1.removeApproval(collab));
        Assert.assertTrue(alt1.addApproval(collab));
        Assert.assertTrue(alt1.removeApproval(collab));
    }

    @Test
    public void testDisapproval(){
        Assert.assertFalse(alt1.removeDisapproval(collab));
        Assert.assertTrue(alt1.addDisapproval(collab));
        Assert.assertTrue(alt1.removeDisapproval(collab));
    }

    @Test
    public void testExclusivity(){
        Assert.assertTrue(alt1.addApproval(collab));
        Assert.assertTrue(alt1.addDisapproval(collab));
        Assert.assertFalse(alt1.removeApproval(collab));
    }

    @Test
    public void testFeedback(){
        Assert.assertTrue(alt1.addFeedback(feedback));
    }
}
