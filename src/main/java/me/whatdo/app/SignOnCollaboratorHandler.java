package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.test.db.ChoiceDAO;
import me.whatdo.app.test.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.ApiResponse;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CollaboratorRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.util.Optional;
import java.util.UUID;

public class SignOnCollaboratorHandler implements RequestHandler<CollaboratorRequest, ApiResponse> {

    public ApiResponse handleRequest(final CollaboratorRequest input, final Context context) {

        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        UUID choiceID;
        ChoiceDAO choiceDAO = new ChoiceDAO();
        CollaboratorDAO colllabDAO = new CollaboratorDAO();

        // Catch any unexpected errors
        try {
            choiceID = UUID.fromString(input.getChoiceID());
            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = choiceDAO.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return new ApiResponse(404, body.toString());
            }

            String name = input.getUsername();
            String password = input.getPassword();

            Optional<Collaborator> collab = colllabDAO.getCollaborator(choiceID,name);

            // * Check: User presence in Choice
            if(!collab.isPresent()){
                body.addProperty("Message", "404 no Collaborator of that name present in Choice");
                return new ApiResponse(404, body.toString());
            }

            // * Check: Password verification
            if(!collab.get().verifyPassword(password)){
                body.addProperty("Message", "401 credential mismatch");
                return new ApiResponse(401, body.toString());
            }

            // Successfully handled and returned
            body.addProperty("authentication", UserAuthHandler.encode(name+":"+password));
            return new ApiResponse(200, UserAuthHandler.encode(name+":"+password));

            //Some other 500 server error arose
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }
}

