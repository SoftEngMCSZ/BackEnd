package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.AlternativeDAO;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.FinalRequest;

import java.util.Optional;
import java.util.UUID;

public class FinalizeChoiceHandler implements RequestHandler<FinalRequest, ApiResponse> {


	public ApiResponse handleRequest(final FinalRequest input, final Context context) {
		JsonObject body = new JsonObject();
		ChoiceDAO choiceDAO = new ChoiceDAO();
		AlternativeDAO altDAO = new AlternativeDAO();
		try {
			if (!validateRequest(input.toJsonObject())) {
				body.addProperty("Message", "400 malformed AdminRequest");
				body.addProperty("Input", input.toJson());
				return new ApiResponse(400, body.toString());
			}

			UUID choiceId, altId;
			try {
				choiceId = UUID.fromString(input.getChoiceId());
				altId = UUID.fromString(input.getAlternativeId());
			} catch (IllegalArgumentException e) {
				body.addProperty("Message", "400 ID malformed");
				return new ApiResponse(400, body.toString());
			}

			Optional<Choice> choiceOpt = choiceDAO.getChoice(choiceId);

			if(!choiceOpt.isPresent()){
				body.addProperty("Message", "404 Choice not found");
				return new ApiResponse(404, body.toString());
			}

			Optional<Alternative> altOpt = altDAO.getAlternative(altId);

			if(!altOpt.isPresent()){
				body.addProperty("Message","404 Alternative not found");
				return new ApiResponse(404, body.toString());
			}

			if (!choiceOpt.get().finalize(altOpt.get())) {
				body.addProperty("Message","500 could not finalize Alternative");
				return new ApiResponse(500, body.toString());
			}

			return new ApiResponse(200, choiceOpt.get().toJson());



		} catch (Exception e) {
			body.addProperty("Message", "500 server error");
			body.addProperty("Error", e.getMessage());
			return new ApiResponse(500, body.toString());
		}
	}

	private static boolean validateRequest(JsonObject req) {
		return req.has("alternativeId");
	}
}

