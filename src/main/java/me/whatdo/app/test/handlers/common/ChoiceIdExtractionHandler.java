package me.whatdo.app.test.handlers.common;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChoiceIdExtractionHandler {

	public static Optional<UUID> extractUUIDFromPathParams(Map<String,String> pathParams) throws IllegalArgumentException {
		if(!pathParams.containsKey("choiceID")) return Optional.empty();

		return Optional.of(UUID.fromString(pathParams.get("choiceID")));
	}

	public static APIGatewayProxyResponseEvent getMissingChoiceIdResponse(Map<String,String> headers) {
		JsonObject body = new JsonObject();
		body.addProperty("Message","400 missing choiceID parameter");
		return new APIGatewayProxyResponseEvent()
					   .withBody(body.toString())
					   .withHeaders(headers)
					   .withStatusCode(400);
	}

	public static APIGatewayProxyResponseEvent getMalformedChoiceIdResponse(Map<String,String> headers, String msg) {
		JsonObject body = new JsonObject();
		body.addProperty("Message", "400 malformed choiceID");
		body.addProperty("Error", msg);
		return new APIGatewayProxyResponseEvent()
					   .withBody(body.toString())
					   .withHeaders(headers)
					   .withStatusCode(400);
	}
}
