package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.request.AdminRequest;

public class AdminReportHandler implements RequestHandler<AdminRequest, ApiResponse> {

    public ApiResponse handleRequest(final AdminRequest input, final Context context) {
        JsonObject body = new JsonObject();
        ChoiceDAO dao = new ChoiceDAO();

        try {
            if (!validateRequest(input.toJson())) {

                body.addProperty("Message", "400 malformed AdminRequest");
                body.addProperty("Input", input.toJson());

                return new ApiResponse(400, body.toString());
            }

            AdminRequest req = new AdminRequest(dao.getAllChoices());

            return new ApiResponse(200, req.toJson());


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
