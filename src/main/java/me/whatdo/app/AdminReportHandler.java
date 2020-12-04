package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.AdminRequest;
import me.whatdo.app.model.request.OpinionRequest;

import java.util.List;

public class AdminReportHandler {

    public ApiResponse handleRequest(final AdminRequest input, final Context context) {
        JsonObject body = new JsonObject();
        ChoiceDAO dao = new ChoiceDAO();
        try {

            if (!validateRequest(input)) {
                body.addProperty("Message", "400 malformed OpinionRequest");
                body.addProperty("Input", input.toJson());
                return new ApiResponse(400, body.toString());
            }


            List<Choice> choices = dao.getAllChoices();

            String json = "{";
            for (int i = 0; i < choices.size(); i++) {
                 json += choices.get(i).toJson();
            }
            json += "}";

            return new ApiResponse(200, json);


        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }

    private static boolean validateRequest(AdminRequest req){
        JsonObject object = req.toJsonObject();
        return object == null;
    }
}
