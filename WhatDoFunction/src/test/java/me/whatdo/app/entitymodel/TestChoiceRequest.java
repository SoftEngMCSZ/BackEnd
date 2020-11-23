package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestChoiceRequest {

    Gson gsonLog, gson;
    Collaborator collab = null;
    ChoiceRequest request = null;
    Alternative alt1, alt2 = null;

    @Before
    public void setupTests() {
        gson = new GsonBuilder().disableHtmlEscaping().create();
        gsonLog = new GsonBuilder().setPrettyPrinting().create();
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("Pet fish");
        alt2 = new Alternative("Pet Dog");
        List<Alternative> alts = new ArrayList<Alternative>();
        alts.add(alt1);
        alts.add(alt2);
        request = new ChoiceRequest("What pet for the kids?",alts,2);
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(request);
    }

    @Test
    public void testSerialize(){
        String jsonStr = gson.toJson(request);
        JsonObject obj = gson.fromJson(jsonStr, JsonObject.class);
        Assert.assertEquals(obj.get("content").toString(), "\"What pet for the kids?\"");
        Assert.assertEquals(obj.getAsJsonArray("alternatives").get(0).toString(), gson.toJson(alt1));
        Assert.assertEquals(obj.get("maxCollaborators").getAsInt(),2);
    }
}
