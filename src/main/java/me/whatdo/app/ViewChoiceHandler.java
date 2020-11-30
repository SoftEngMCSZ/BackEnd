package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.CreateChoiceRequest;
import me.whatdo.app.model.request.ViewChoiceRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import javax.swing.text.View;
import java.util.Optional;
import java.util.UUID;

public class ViewChoiceHandler implements RequestHandler<ViewChoiceRequest, ApiResponse> {

    public ApiResponse handleRequest(final ViewChoiceRequest request, final Context context) {

        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        UUID choiceID;
        ChoiceDAO dao = new ChoiceDAO();

        // Catch any unexpected errors
        try {

            if (!validateRequest(request)) {
                body.addProperty("Message", "400 malformed ChoiceRequest");
                body.addProperty("Input", request.toJson());
                return new ApiResponse(400, body.toString());
            }

            try {
                choiceID = UUID.fromString(request.getID());
            } catch (IllegalArgumentException e){
                body.addProperty("Message", "400 ChoiceID malformed");
                return new ApiResponse(400, body.toString());
            }

            Optional<Choice> choice = dao.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return new ApiResponse(404, body.toString());
            }

            if(!UserAuthHandler.isUserAuthenticated(request.getAuthentication(), choiceID)){
                body.addProperty("Message", "401 user not signed signed in to Choice");
                return new ApiResponse(401, body.toString());
            }

            // Successfully handled and returned
            return new ApiResponse(200, choice.get().toJson());

            //Some other 500 server error arose
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());

        }
    }

    private static boolean validateRequest(ViewChoiceRequest req){
        JsonObject object = req.toJsonObject();
        return object.has("choiceID") &&
                object.has("authentication") &&
                !object.get("authentication").toString().isEmpty();
    }
}

