package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestChoice {

    Alternative alt1, alt2 = null;
    Collaborator collab = null;
    ChoiceRequest request = null;
    Choice choice = null;

    @Before
    public void setupTests() {
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("Pet fish");
        alt2 = new Alternative("Pet Dog");
        List<Alternative> alts = new ArrayList<Alternative>();
        alts.add(alt1);
        alts.add(alt2);
        request = new ChoiceRequest("What pet for the kids?",alts,1);
        choice = new Choice(request);
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(choice);
    }

    @Test
    public void testSerialize(){
        JsonObject obj = choice.toJsonObject();
        Assert.assertEquals(obj.get("contents").toString(), "\"What pet for the kids?\"");
        Assert.assertEquals(obj.getAsJsonArray("alternatives").get(0).toString(), alt1.toJson());
        Assert.assertEquals(obj.get("maxCollaborators").getAsInt(),1);
    }

    @Test
    public void testDeserialize(){
        choice.addCollaborator(collab);
        String jsonStr = choice.toJson();
        Choice choice2 = Choice.fromJson(jsonStr);
        Assert.assertTrue(choice2.hasCollaborator(collab));
    }

    @Test
    public void testCollaborator(){
        Assert.assertFalse(choice.hasCollaborator(collab));
        Assert.assertTrue(choice.addCollaborator(collab));
        Assert.assertTrue(choice.hasCollaborator(collab));
        Assert.assertFalse(choice.addCollaborator(collab));
    }

    @Test public void testFinalise(){
        Assert.assertFalse(choice.selectAlternative(new Alternative("No")));
        Assert.assertTrue(choice.selectAlternative(alt1));
    }



}
