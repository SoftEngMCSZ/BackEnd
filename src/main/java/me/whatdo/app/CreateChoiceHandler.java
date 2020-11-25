package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.ChoiceRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateChoiceHandler implements RequestHandler<ChoiceRequest, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final ChoiceRequest input, final Context context) {

        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();
        logger.log(gson.toJson(context));
        logger.log(gson.toJson(input));
        logger.log(input.toJson());

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
            request = input;
            if (!validateRequest(request)) {
                body.addProperty("Message", "400 malformed ChoiceRequest");
                body.addProperty("Input", input.toJson());
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

