package me.whatdo.app.entitymodel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;


public class TestCollaborator {

    Collaborator collab = null;

    @Before
    public void setupTests(){
        collab = Collaborator.fromPlaintextPassword("Maxy", "Baboo");
    }

    @Test
    public void testCollaboratorConstructor(){
        Assert.assertNotNull(collab);
        Assert.assertEquals(collab.getName(), "Maxy");
    }

    @Test
    public void testSerialize(){
        String testStr = "{\"id\":\""+collab.getId()+"\",\"name\":\"Maxy\"}";
        Assert.assertEquals(collab.toJson(),testStr);
    }

    @Test
    public void testDeserialize(){
        Collaborator max = Collaborator.fromJson("{\"id\":\""+UUID.randomUUID()+"\",\"name\":\"Maxy\",\"password\":\"Baboo\"}");
        Assert.assertTrue(max.verifyPassword("Baboo"));
    }

    @Test
    public void testEquality(){
        Collaborator max = new Collaborator(collab.getId(),"Maxy");
        Assert.assertEquals(max,collab);
        Assert.assertEquals(max.hashCode(),collab.hashCode());
    }

    @Test
    public void testPassword(){
        Assert.assertTrue(collab.verifyPassword("Baboo"));
        Assert.assertFalse(collab.verifyPassword("Yadoo"));
    }
}
