package app.test.entitymodel;


import com.google.gson.JsonObject;
import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CreateChoiceRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestChoiceRequest {

    Collaborator collab = null;
    CreateChoiceRequest request = null;
    Alternative alt1, alt2 = null;

    @Before
    public void setupTests() {
        collab = new Collaborator("Maxy", "Baboo");
        alt1 = new Alternative("Pet fish");
        alt2 = new Alternative("Pet Dog");
        List<Alternative> alts = new ArrayList<Alternative>();
        alts.add(alt1);
        alts.add(alt2);
        request = new CreateChoiceRequest("What pet for the kids?",alts,2);
    }

    @Test
    public void testConstructor(){
        Assert.assertNotNull(request);
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
