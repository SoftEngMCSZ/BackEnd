package app.test.model.request;


import com.google.gson.JsonObject;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.CreateChoiceRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCreateChoiceRequest {

    Collaborator collab = null;
    CreateChoiceRequest request = null;
    Alternative alt1, alt2 = null;

    @Before
    public void setupTests() {
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("Pet fish");
        alt2 = new Alternative("Pet Dog");
        List<Alternative> alts = new ArrayList<>();
        alts.add(alt1);
        alts.add(alt2);
        request = new CreateChoiceRequest("What pet for the kids?",alts,2);
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(request);
    }

    @Test
    public void setters() {
        CreateChoiceRequest req = new CreateChoiceRequest();
        List<Alternative> alts = Arrays.asList(
                new Alternative("To be"),
                new Alternative("Not to be")
        );
        req.setQuestion("To be or not to be?");
        req.setAlternatives(alts);
        req.setMaxCollaborators(2);

        Assert.assertEquals("To be or not to be?",req.question);
        Assert.assertEquals(alts,req.alternatives);
        Assert.assertEquals(2,req.maxCollaborators);
    }

    @Test
    public void testSerialize(){
        JsonObject obj = request.toJsonObject();
        Assert.assertEquals(obj.get("question").toString(), "\"What pet for the kids?\"");
        Assert.assertEquals(obj.getAsJsonArray("alternatives").get(0).toString(), alt1.toJson());
        Assert.assertEquals(obj.get("maxCollaborators").getAsInt(),2);
    }

    @Test
    public void testDeserialize(){
        CreateChoiceRequest request2 = CreateChoiceRequest.fromJson(request.toJson());
        JsonObject obj = request2.toJsonObject();
        Assert.assertEquals(obj.get("question").toString(), "\"What pet for the kids?\"");
        Assert.assertEquals(obj.getAsJsonArray("alternatives").get(0).toString(), alt1.toJson());
        Assert.assertEquals(obj.get("maxCollaborators").getAsInt(),2);
    }
}
