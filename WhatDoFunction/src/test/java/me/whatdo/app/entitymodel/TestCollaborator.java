package me.whatdo.app.entitymodel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestCollaborator {

    Collaborator collab = null;

    @Before
    public void setupTests(){
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
        Assert.assertEquals(collab.toJson(),testStr);
    }

    @Test
    public void testDeserialize(){
        Collaborator max = Collaborator.fromJson("{\"name\":\"Maxy\",\"password\":\"Baboo\"}");
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
