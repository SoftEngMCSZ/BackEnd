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
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.CollaboratorRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SignUpCollaboratorHandler implements RequestHandler<CollaboratorRequest, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final CollaboratorRequest input, final Context context) {
        Map<String,String>  responseHeaders, pathParams, queryParams;

        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();
        logger.log(gson.toJson(context));
        logger.log(gson.toJson(input));

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
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(400);
            }

            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = choiceDAO.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(404);
            }


            String name = input.getUsername();
            String password = input.getPassword();
            if(name.isEmpty()){
                body.addProperty("Message","400 username not present");
                return response.withHeaders(responseHeaders).withBody(body.toString()).withStatusCode(400);
            }
            if(password.isEmpty()) {
                collab = new Collaborator(name);
            } else {
                collab = new Collaborator(name,password);
            }

            if(!colllabDAO.addCollaborator(choiceID,collab)) {
                body.addProperty("Message", "400 unable to add Collaborator to DB");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(400);
            }

            // Successfully handled and returned
            body.addProperty("authentication", UserAuthHandler.encode(name+":"+password));
            return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(201);

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

