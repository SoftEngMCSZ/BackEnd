package me.whatdo.app.handlers.alternative;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.AlternativeDAO;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.db.FeedbackDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.entity.Feedback;
import me.whatdo.app.model.request.FeedbackRequest;

import java.util.Optional;
import java.util.UUID;

public class FeedbackHandler implements RequestHandler<FeedbackRequest, ApiResponse> {

		public ApiResponse handleRequest(final FeedbackRequest input, final Context context) {
			JsonObject body = new JsonObject();
			ChoiceDAO choiceDAO = new ChoiceDAO();
			AlternativeDAO altDAO = new AlternativeDAO();
			FeedbackDAO feedbackDAO = new FeedbackDAO();
			CollaboratorDAO collabDAO = new CollaboratorDAO();
			try {
				if (!validateRequest(input.toJsonObject())) {
					body.addProperty("Message", "400 malformed FeedbackRequest");
					body.addProperty("Input", input.toJson());
					return new ApiResponse(400, body.toString());
				}

				UUID choiceId, altId, collabId;
				try {
					choiceId = UUID.fromString(input.getChoiceId());
					altId = UUID.fromString(input.getAlternativeId());
					collabId = UUID.fromString(input.getCollaboratorId());
				} catch (IllegalArgumentException e) {
					body.addProperty("Message", "400 ID malformed");
					return new ApiResponse(400, body.toString());
				}

				Optional<Choice> choiceOpt = choiceDAO.getChoice(choiceId);

				if (!choiceOpt.isPresent()) {
					body.addProperty("Message", "404 Choice not found");
					return new ApiResponse(404, body.toString());
				} else if (choiceOpt.get().getFinalAlternative().isPresent()) {
					body.addProperty("Message", "400 Choice already finalized");
					return new ApiResponse(400, body.toString());
				}

				Optional<Collaborator> collabOpt = collabDAO.getCollaborator(collabId);

				if (!collabOpt.isPresent()) {
					body.addProperty("Message", "404 Collaborator not found");
					return new ApiResponse(404, body.toString());
				}

				Optional<Alternative> altOpt = altDAO.getAlternative(altId);

				if (!altOpt.isPresent()) {
					body.addProperty("Message", "404 Alternative not found");
					return new ApiResponse(404, body.toString());
				}

				if (!feedbackDAO.addFeedback(altId, new Feedback(input))) {
					body.addProperty("Message", "400 could not add feedback to Alternative");
					return new ApiResponse(400, body.toString());
				}
				choiceOpt = choiceDAO.getChoice(choiceId);
				assert choiceOpt.isPresent();

				return new ApiResponse(200, choiceOpt.get().toJson());

			} catch (Exception e) {
				body.addProperty("Message", "500 server error");
				body.addProperty("Error", e.getMessage());
				return new ApiResponse(500, body.toString());
			}
		}

		private static boolean validateRequest(JsonObject req) {
			return req.has("alternativeId") &&
						   req.has("collaboratorId") &&
						   req.has("contents") &&
						   req.has("choiceId");
		}
}
