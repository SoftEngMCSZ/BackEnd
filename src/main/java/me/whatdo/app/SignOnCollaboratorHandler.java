package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CollaboratorRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import me.whatdo.app.handlers.common.ChoiceIdExtractionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SignOnCollaboratorHandler implements RequestHandler<CollaboratorRequest, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final CollaboratorRequest input, final Context context) {
        Map<String,String>  responseHeaders, pathParams, queryParams;

        // Response related instantiation
        // Map<String,String>  responseHeaders = new HashMap<>();
        responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(responseHeaders);
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
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(404);
            }

            String name = input.getUsername();
            String password = input.getPassword();

            Optional<Collaborator> collab = colllabDAO.getCollaborator(choiceID,name);

            // * Check: User presence in Choice
            if(!collab.isPresent()){
                body.addProperty("Message", "404 no Collaborator of that name present in Choice");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(404);
            }

            // * Check: Password verification
            if(!collab.get().verifyPassword(password)){
                body.addProperty("Message", "401 credential mismatch");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(401);
            }

            // Successfully handled and returned
            body.addProperty("authentication", UserAuthHandler.encode(name+":"+password));
            return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(200);

            //Some other 500 server error arose
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return response
                    .withBody(body.toString())
                    .withHeaders(responseHeaders)
                    .withStatusCode(500);
        }
    }
}

