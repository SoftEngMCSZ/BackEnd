package me.whatdo.app.handlers.choice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ChoiceRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateChoiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        JsonObject body = new JsonObject();
        try {
            if (input.getHttpMethod().equals("POST")) {
                ChoiceRequest request = ChoiceRequest.fromJson(input.getBody());
                if (validateRequest(request)) {
                    Choice choice = new Choice(request);
                    ChoiceDAO dao = new ChoiceDAO();
                    try {
                        if (dao.addChoice(choice)) {
                            return response.withBody(choice.toJson()).withHeaders(headers).withStatusCode(201);
                        } else {
                            body.addProperty("Message", "400 unable to insert Choice to DB");
                            return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
                        }
                    } catch (Exception e) {
                        body.addProperty("Message", "500 server error");
                        body.addProperty("Error", e.getMessage());
                        return response.withBody(body.toString()).withHeaders(headers).withStatusCode(500);
                    }
                } else {
                    body.addProperty("Message", "400 malformed ChoiceRequest");
                    return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
                }
            } else {
                body.addProperty("Message", "405 method not allowed");
                return response.withBody(body.toString()).withHeaders(headers).withStatusCode(405);
            }
        } catch (Exception e){
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return response
                    .withBody(body.toString())
                    .withStatusCode(500);
        }

    }

    private static boolean validateRequest(ChoiceRequest req){
        JsonObject object = req.toJsonObject();
        return object.has("question") &&
            object.has("alternatives") &&
            object.has("maxCollaborators");
    }
}

