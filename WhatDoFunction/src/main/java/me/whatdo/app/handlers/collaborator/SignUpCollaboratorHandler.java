package me.whatdo.app.handlers.collaborator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SignUpCollaboratorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
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
        Collaborator collab;
        ChoiceDAO choiceDAO = new ChoiceDAO();
        CollaboratorDAO colllabDAO = new CollaboratorDAO();

        // Catch any unexpected errors
        try {
            // First Check: HTTP Method
            if (!input.getHttpMethod().equals("POST")) {
                body.addProperty("Message", "405 method not allowed");
                return response
                        .withBody(body.toString())
                        .withHeaders(responseHeaders)
                        .withStatusCode(405);
            }
            // Second Check: choiceID presence in path
            pathParams = input.getPathParameters();
            if (!pathParams.containsKey("choiceID")) {
                body.addProperty("Message", "400 missing choiceID paramater");
                return response
                        .withBody(body.toString())
                        .withHeaders(responseHeaders)
                        .withStatusCode(400);
            }
            // Third Check: Malformed ID
            try {
                choiceID = UUID.fromString(pathParams.get("choiceID"));
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

            // * Check: Username presence
            queryParams = input.getQueryStringParameters();
            if(!queryParams.containsKey("name")) {
                body.addProperty("Message", "400 username not present");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(400);
            }

            String name = queryParams.get("name");
            String password = queryParams.get("password");

            if(password != null) {
                collab = Collaborator.fromPlaintextPassword(name,password);
            } else {
                collab = new Collaborator(name);
            }

            if(!colllabDAO.addCollaborator(choiceID,collab)) {
                body.addProperty("Message", "400 unable to add Collaborator to DB");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(400);
            }

            // Successfully handled and returned
            body.addProperty("auth-token", UserAuthHandler.encode(name+":"+password));
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

