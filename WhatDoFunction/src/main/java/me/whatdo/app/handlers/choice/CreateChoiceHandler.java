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
import java.util.UUID;

public class CreateChoiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        //Response related Instantiation
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        ChoiceRequest request;
        Choice choice;
        ChoiceDAO dao;

        try {
            // First Check: HTTP Method
            if (!input.getHttpMethod().equals("POST")) {
                body.addProperty("Message", "405 method not allowed");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(405);
            }
            // Second Check: Validate request schema
            request = ChoiceRequest.fromJson(input.getBody());
            if (!validateRequest(request)) {
                body.addProperty("Message", "400 malformed ChoiceRequest");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
            }
            choice = new Choice(request);
            dao = new ChoiceDAO();
            // Third Check: presence of Choice in database already
            if (!dao.addChoice(choice)) {
                body.addProperty("Message", "400 unable to insert Choice to DB");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
            }
            // Successfully returned Choice
            return response.withBody(choice.toJson()).withHeaders(headers).withStatusCode(201);

            // Catch unexpected server errors
        } catch (Exception e){
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return response
                    .withBody(body.toString())
                    .withStatusCode(500);
        }
    }
    // Used to validate all required fields present
    private static boolean validateRequest(ChoiceRequest req){
        JsonObject object = req.toJsonObject();
        return object.has("question") &&
            object.has("alternatives") &&
            object.has("maxCollaborators");
    }
}

