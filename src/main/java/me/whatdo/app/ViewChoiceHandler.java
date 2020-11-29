package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.test.db.ChoiceDAO;
import me.whatdo.app.entitymodel.ApiResponse;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ViewChoiceRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.util.Optional;
import java.util.UUID;

public class ViewChoiceHandler implements RequestHandler<ViewChoiceRequest, ApiResponse> {

    public ApiResponse handleRequest(final ViewChoiceRequest input, final Context context) {

        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        UUID choiceID;
        ChoiceDAO dao = new ChoiceDAO();

        // Catch any unexpected errors
        try {

            choiceID = UUID.fromString(input.getID());

            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = dao.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return new ApiResponse(404, body.toString());
            }

            // Sixth Check: Choice Membership
            if(!UserAuthHandler.isUserAuthenticated(input.getAuthentication(), choiceID)){
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
}

