package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestCollaborator {

    Gson gsonLog, gson;
    Collaborator collab = null;

    @Before
    public void setupTests(){
        gson = new GsonBuilder().disableHtmlEscaping().create();
        gsonLog = new GsonBuilder().setPrettyPrinting().create();
        collab = new Collaborator("Maxy", "Baboo");
    }

    @Test
    public void testCollaboratorConstructor(){
        Assert.assertNotNull(collab);
        Assert.assertEquals(collab.getName(), "Maxy");
    }

    @Test
    public void testSerialize(){
        String testStr = "{\"name\":\"Maxy\"}";
        Assert.assertEquals(gson.toJson(collab),testStr);
    }

    @Test
    public void testDeserialize(){
        Collaborator max = gson.fromJson("{\"name\":\"Maxy\",\"password\":\"Baboo\"}", Collaborator.class);
        Assert.assertTrue(max.verifyPassword("Baboo"));
    }

    @Test
    public void testEquality(){
        Collaborator max = new Collaborator("Maxy");
        Assert.assertEquals(max,collab);
        Assert.assertEquals(max.hashCode(),collab.hashCode());
    }

    @Test
    public void testPassword(){
        Assert.assertTrue(collab.verifyPassword("Baboo"));
        Assert.assertFalse(collab.verifyPassword("Yadoo"));
    }
}
