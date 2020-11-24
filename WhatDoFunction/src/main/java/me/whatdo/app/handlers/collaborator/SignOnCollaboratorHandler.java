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
import me.whatdo.app.handlers.common.ChoiceIdExtractionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SignOnCollaboratorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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
        ChoiceDAO choiceDAO = new ChoiceDAO();
        CollaboratorDAO colllabDAO = new CollaboratorDAO();

        // Catch any unexpected errors
        try {
            // First Check: HTTP Method
            if (!input.getHttpMethod().equals("GET")) {
                body.addProperty("Message", "405 method not allowed");
                return response
                        .withBody(body.toString())
                        .withHeaders(responseHeaders)
                        .withStatusCode(405);
            }
            // Second Check: choiceID presence in path
            try {
                Optional<UUID> maybe_choice_id = ChoiceIdExtractionHandler.extractUUIDFromPathParams(input.getPathParameters());
                if(maybe_choice_id.isPresent()) {
                    choiceID = maybe_choice_id.get();
                } else {
                    return ChoiceIdExtractionHandler.getMissingChoiceIdResponse(responseHeaders);
                }
            } catch (IllegalArgumentException e) {
                return ChoiceIdExtractionHandler.getMalformedChoiceIdResponse(responseHeaders,e.getMessage());
            }

            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = choiceDAO.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(404);
            }

            // * Check: Username presence in request
            queryParams = input.getQueryStringParameters();
            if(!queryParams.containsKey("name")) {
                body.addProperty("Message", "400 username not present");
                return response.withBody(body.toString()).withHeaders(responseHeaders).withStatusCode(400);
            }

            String name = queryParams.get("name");
            String password = queryParams.get("password");

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
            body.addProperty("auth-token", UserAuthHandler.encode(name+":"+password));
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

