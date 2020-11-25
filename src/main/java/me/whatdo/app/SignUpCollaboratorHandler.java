package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.ApiResponse;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CollaboratorRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SignUpCollaboratorHandler implements RequestHandler<CollaboratorRequest, ApiResponse> {

    public ApiResponse handleRequest(final CollaboratorRequest input, final Context context) {

        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();
        logger.log(gson.toJson(context));
        logger.log(gson.toJson(input));

        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        UUID choiceID;
        Collaborator collab;
        ChoiceDAO choiceDAO = new ChoiceDAO();
        CollaboratorDAO colllabDAO = new CollaboratorDAO();

        // Catch any unexpected errors
        try {

            // Third Check: Malformed ID
            try {
                choiceID = UUID.fromString(input.getChoiceID());
            } catch (IllegalArgumentException e) {
                body.addProperty("Message", "400 malformed choiceID");
                body.addProperty("Error", e.getMessage());
                return new ApiResponse(400, body.toString());
            }

            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = choiceDAO.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return new ApiResponse(404, body.toString());
            }


            String name = input.getUsername();
            String password = input.getPassword();
            if(name.isEmpty()){
                body.addProperty("Message","400 username not present");
                return new ApiResponse(400, body.toString());
            }
            if(password.isEmpty()) {
                collab = new Collaborator(name);
            } else {
                collab = Collaborator.fromPlaintextPassword(name,password);
            }

            if(!colllabDAO.addCollaborator(choiceID,collab)) {
                body.addProperty("Message", "400 unable to add Collaborator to DB");
                return new ApiResponse(400, body.toString());
            }

            // Successfully handled and returned
            body.addProperty("authentication", UserAuthHandler.encode(name+":"+password));
            return new ApiResponse(200, body.toString());

            //Some other 500 server error arose
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }
}

