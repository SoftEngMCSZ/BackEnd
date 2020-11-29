package me.whatdo.app.test.entitymodel;

import com.google.gson.*;

import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class TestAlternative {
    Collaborator collab = null;
    Alternative alt1,alt2 = null;
    Feedback feedback = null;



    @Before
    public void setupTests() {
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("We eat pizza?");
        feedback = new Feedback(alt1.getId(), collab, Date.from(Instant.now()), "But I don't like pizza :(");
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(alt1);
        Assert.assertEquals(alt1.getContents(), "We eat pizza?");
    }

    @Test
    public void testSerialize(){
        JsonObject obj = alt1.toJsonObject();
        Assert.assertEquals(obj.get("description").toString(), "\"We eat pizza?\"");
    }

    @Test
    public void testDeserialize(){
        String jsonStr = alt1.toJson();
        alt2 = Alternative.fromJson(jsonStr);
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
