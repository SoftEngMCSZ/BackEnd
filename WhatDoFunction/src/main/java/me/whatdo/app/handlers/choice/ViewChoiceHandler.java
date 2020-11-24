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
import java.util.Optional;
import java.util.UUID;

public class ViewChoiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        JsonObject body = new JsonObject();
        try {
            if (input.getHttpMethod().equals("GET")) {
                UUID choiceID;
                Map<String, String> pathParams = input.getPathParameters();
                if (pathParams.containsKey("choiceID")) {
                    try {
                        choiceID = UUID.fromString(pathParams.get("choiceID"));
                    } catch (IllegalArgumentException e) {
                        body.addProperty("Message","400 malformed choiceID");
                        return response.withBody(body.toString()).withHeaders(headers).withStatusCode(400);
                    }
                    ChoiceDAO dao = new ChoiceDAO();
                    Optional<Choice> choice = dao.getChoice(choiceID);
                    if(choice.isPresent()){
                        return response.withBody(choice.get().toJson()).withHeaders(headers).withStatusCode(200);
                    } else {
                        body.addProperty("Message","404 Choice not found");
                        body.addProperty("Received", choiceID.toString());
                        return response.withBody(body.toString()).withHeaders(headers).withStatusCode(404);
                    }
                } else {
                    body.addProperty("Message", "400 missing choiceID paramater");
                    return response
                            .withBody(body.toString())
                            .withHeaders(headers)
                            .withStatusCode(500);
                }
            } else {
                body.addProperty("Message", "405 method not allowed");
                return response
                        .withBody(body.toString())
                        .withHeaders(headers)
                        .withStatusCode(405);
            }
        } catch (Exception e) {
            body.addProperty("Message", "500 server error");
            return response
                    .withBody(body.toString())
                    .withHeaders(headers)
                    .withStatusCode(500);
        }
    }
}

