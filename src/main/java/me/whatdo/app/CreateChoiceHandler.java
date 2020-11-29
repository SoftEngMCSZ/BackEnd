package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.whatdo.app.test.db.ChoiceDAO;
import me.whatdo.app.entitymodel.ApiResponse;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.CreateChoiceRequest;

public class CreateChoiceHandler implements RequestHandler<CreateChoiceRequest, ApiResponse> {

    public ApiResponse handleRequest(final CreateChoiceRequest input, final Context context) {

        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();
        logger.log(gson.toJson(context));
        logger.log(gson.toJson(input));
        logger.log(input.toJson());

        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        CreateChoiceRequest request;
        Choice choice;
        ChoiceDAO dao;

        try {
            request = input;
            if (!validateRequest(request)) {
                body.addProperty("Message", "400 malformed ChoiceRequest");
                body.addProperty("Input", input.toJson());
                return new ApiResponse(400, body.toString());
            }
            choice = new Choice(request);
            dao = new ChoiceDAO();
            // Third Check: presence of Choice in database already
            if (!dao.addChoice(choice)) {
                body.addProperty("Message", "400 unable to insert Choice to DB");
                return new ApiResponse(400, body.toString());
            }
            // Successfully returned Choice
            return new ApiResponse(200,choice.toJson());
            // Catch unexpected server errors
        } catch (Exception e){
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }
    // Used to validate all required fields present
    private static boolean validateRequest(CreateChoiceRequest req){
        JsonObject object = req.toJsonObject();
        return object.has("question") &&
            object.has("alternatives") &&
            object.has("maxCollaborators");
    }
}

