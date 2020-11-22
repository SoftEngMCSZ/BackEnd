package me.whatdo.app.entitymodel;

import com.google.gson.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAlternative {
    Gson gsonLog, gson;
    Collaborator collab = null;
    Alternative alt1;
    Alternative alt2;
    ChoiceRequest request;
    Choice choice;



    @Before
    public void setupTests() {
        gson = new Gson();
        gsonLog = new GsonBuilder().setPrettyPrinting().create();
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("We eat pizza?");
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

    }
}
