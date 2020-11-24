package me.whatdo.app.handlers.choice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ChoiceRequest;
import me.whatdo.app.handlers.auth.UserAuthHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ViewChoiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        // Response related instantiation
        Map<String,String>  headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        UUID choiceID;
        ChoiceDAO dao = new ChoiceDAO();

        // Catch any unexpected errors
        try {
            // First Check: HTTP Method
            if (!input.getHttpMethod().equals("GET")) {
                body.addProperty("Message", "405 method not allowed");
                return response
                        .withBody(body.toString())
                        .withHeaders(headers)
                        .withStatusCode(405);
            }
            // Second Check: choiceID presence in path
            Map<String, String> pathParams = input.getPathParameters();
            if (!pathParams.containsKey("choiceID")) {
                body.addProperty("Message", "400 missing choiceID paramater");
                return response
                        .withBody(body.toString())
                        .withHeaders(headers)
                        .withStatusCode(400);
            }
            // Third Check: Malformed ID
            try {
                choiceID = UUID.fromString(pathParams.get("choiceID"));
            } catch (IllegalArgumentException e) {
                body.addProperty("Message", "400 malformed choiceID");
                body.addProperty("Error", e.getMessage());
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
            }


            // Fourth Check: Choice presence in Database
            Optional<Choice> choice = dao.getChoice(choiceID);
            if (!choice.isPresent()) {
                body.addProperty("Message", "404 Choice not found");
                body.addProperty("Received", choiceID.toString());
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(404);
            }

            // Fifth Check : Authentication Header
            Map<String, String > requestHeader = input.getHeaders();
            if(!requestHeader.containsKey("Authentication")){
                body.addProperty("Message", "401 user not signed in to WhatDo");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(401);
            }

            // Sixth Check: Choice Membership
            if(!UserAuthHandler.isUserAuthenticated(requestHeader.get("Authentication"), choiceID)){
                System.out.println(requestHeader.get("Authentication"));
                body.addProperty("Message", "401 user not signed signed in to Choice");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(401);
            }

            // Successfully handled and returned
            return response.withBody(choice.get().toJson()).withHeaders(headers).withStatusCode(200);

            //Some other 500 server error arose
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return response
                    .withBody(body.toString())
                    .withHeaders(headers)
                    .withStatusCode(500);
        }
    }
}

