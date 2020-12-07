package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.handlers.auth.UserAuthHandler;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.request.CollaboratorRequest;

import java.util.Optional;
import java.util.UUID;

public class RegisterCollaboratorHandler implements RequestHandler<CollaboratorRequest, ApiResponse> {

	public ApiResponse handleRequest(final CollaboratorRequest input, final Context context) {
		JsonObject body = new JsonObject();

		// Catch any unexpected errors
		try {

			// Third Check: Malformed ID
			UUID choiceID;
			try {
				choiceID = UUID.fromString(input.getChoiceID());
			} catch (IllegalArgumentException e) {
				body.addProperty("Message", "400 malformed choiceID");
				body.addProperty("Error", e.getMessage());
				return new ApiResponse(400, body.toString());
			}

			ChoiceDAO choiceDao = new ChoiceDAO();
			// Fourth Check: Choice presence in Database
			Optional<Choice> choice = choiceDao.getChoice(choiceID);
			if (!choice.isPresent()) {
				body.addProperty("Message", "404 Choice not found");
				body.addProperty("Received", choiceID.toString());
				return new ApiResponse(404, body.toString());
			}


			String name = input.getUsername();
			String password = input.getPassword();
			if (name.isEmpty()) {
				body.addProperty("Message", "400 username not present");
				return new ApiResponse(400, body.toString());
			}

			CollaboratorDAO colllabDao = new CollaboratorDAO();

			Optional<Collaborator> collabOpt = colllabDao.getCollaborator(choiceID, name);
			if (collabOpt.isPresent()) {
				if (!collabOpt.get().verifyPassword(password)) {
					body.addProperty("Message", "401 credential mismatch");
					return new ApiResponse(401, body.toString());
				}
				// Successfully handled and returned
				body.addProperty("authentication", UserAuthHandler.encode(name + ":" + password));
				body.addProperty("id", collabOpt.get().getId().toString());
				return new ApiResponse(200, body.toString());

			} else {
				if (choice.get().getCollaborators().size() == choice.get().getMaxCollaborators()) {
					body.addProperty("Message", "400 maximum collaborators reached");
					return new ApiResponse(400, body.toString());
				}
				Collaborator collab;
				if (password.isEmpty()) {
					collab = new Collaborator(name);
				} else {
					collab = Collaborator.fromPlaintextPassword(name, password);
				}

				if (!colllabDao.addCollaborator(choiceID, collab)) {
					body.addProperty("Message", "400 unable to add Collaborator to DB");
					return new ApiResponse(400, body.toString());
				}
				// Successfully handled and returned
				body.addProperty("authentication", UserAuthHandler.encode(name + ":" + password));
				body.addProperty("id", collab.getId().toString());
				return new ApiResponse(201, body.toString());
			}

			//Some other 500 server error arose
		} catch (Exception e) {
			body.addProperty("Message", "500 server error");
			body.addProperty("Error", e.getMessage());
			return new ApiResponse(500, body.toString());
		}
	}
}

