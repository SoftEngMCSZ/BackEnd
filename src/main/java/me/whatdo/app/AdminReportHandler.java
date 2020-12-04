package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.request.AdminRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class AdminReportHandler implements RequestHandler<AdminRequest, ApiResponse> {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    ChoiceDAO dao = new ChoiceDAO();

    public ApiResponse handleRequest(final AdminRequest input, final Context context) {
        JsonObject body = new JsonObject();

        try {
            if (!validateRequest(input.toJson())) {

                body.addProperty("Message", "400 malformed AdminRequest");
                body.addProperty("Input", input.toJson());



                return new ApiResponse(400, body.toString());
            }

            body.add("choices", gson.toJsonTree(dao.getAllChoices()));

            return new ApiResponse(200, body.toString());



        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }


    private static boolean validateRequest(String req){
        return req.equals("{}");
    }
}
