package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.CreateChoiceRequest;

public class CreateChoiceHandler implements RequestHandler<CreateChoiceRequest, ApiResponse> {

	public ApiResponse handleRequest(final CreateChoiceRequest request, final Context context) {

		JsonObject body = new JsonObject();

		try {
			if (!validateRequest(request)) {
				body.addProperty("Message", "400 malformed ChoiceRequest");
				body.addProperty("Input", request.toJson());
				return new ApiResponse(400, body.toString());
			}

			Choice choice = new Choice(request);
			ChoiceDAO choiceDao = new ChoiceDAO();
			// Third Check: presence of Choice in database already
			if (!choiceDao.addChoice(choice)) {
				body.addProperty("Message", "400 unable to insert Choice to DB");
				return new ApiResponse(400, body.toString());
			}
			// Successfully returned Choice
			return new ApiResponse(200, choice.toJson());
			// Catch unexpected server errors
		} catch (Exception e) {
			body.addProperty("Message", "500 server error");
			body.addProperty("Error", e.getMessage());
			return new ApiResponse(500, body.toString());
		}
	}

	// Used to validate all required fields present
	private static boolean validateRequest(CreateChoiceRequest req) {
		JsonObject object = req.toJsonObject();
		return object.has("question") &&
					   object.has("alternatives") &&
					   object.has("maxCollaborators");
	}
}
