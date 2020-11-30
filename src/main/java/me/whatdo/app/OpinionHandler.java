package me.whatdo.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import me.whatdo.app.db.*;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.request.OpinionRequest;

import java.util.Optional;

public class OpinionHandler implements RequestHandler<OpinionRequest, ApiResponse> {

    public ApiResponse handleRequest(final OpinionRequest input, final Context context) {
        JsonObject body = new JsonObject();

        // Business Logic Instantiation
        OpinionRequest request;
        Opinion opinion;
        OpinionDAO opinionDao = new OpinionDAO();
        ChoiceDAO choiceDAO = new ChoiceDAO();

        try {
            request = input;
            if (!validateRequest(request)) {
                body.addProperty("Message", "400 malformed OpinionRequest");
                body.addProperty("Input", input.toJson());
                return new ApiResponse(400, body.toString());
            }

            Optional<Choice> maybeChoice = choiceDAO.getChoice(request.getChoiceId());
            if(!maybeChoice.isPresent()) {
                body.addProperty("Message","404 Choice not found");
                body.addProperty("Collaborator",request.getChoiceId().toString());
                return new ApiResponse(404,body.toString());
            }

            Choice choice = maybeChoice.get();

            // Search through collaborators to make sure the id is present
            if(choice.getCollaborators().stream().noneMatch(c->c.getId().equals(request.getCollabId()))) {
                body.addProperty("Message","404 Collaborator not found");
                body.addProperty("Collaborator",request.getCollabId().toString());
                return new ApiResponse(404,body.toString());
            }

            // Search through alternatives to make sure the id is present
            if(choice.getAlternatives().stream().noneMatch(c->c.getId().equals(request.getAlternativeId()))) {
                body.addProperty("Message","404 Alternative not found");
                body.addProperty("Collaborator",request.getAlternativeId().toString());
                return new ApiResponse(404,body.toString());
            }

            try {
                opinion = Opinion.valueOf(request.getOpinionType().toUpperCase());

                boolean worked;
                if("add".equals(request.getActionType())) {
                    worked = opinionDao.addOpinion(request.getAlternativeId(),request.getCollabId(),opinion);

                } else if ("remove".equals(request.getActionType())) {
                    worked = opinionDao.deleteOpinion(request.getAlternativeId(),request.getCollabId(),opinion);

                } else {
                    body.addProperty("Message","400 invalid action type type");
                    body.addProperty("ActionType",request.getActionType());
                    return new ApiResponse(400,body.toString());
                }

                if(!worked) {
                    body.addProperty("Message", "400 unable to " +
                                                        request.getActionType() + " " +
                                                        opinion.toString().toLowerCase() +
                                                        " by collaborator " + request.getCollabId() +
                                                        " to alternative " + request.getAlternativeId());
                    return new ApiResponse(400, body.toString());
                }

                maybeChoice = choiceDAO.getChoice(request.getChoiceId());
                // If there was a problem, the previous fetch would catch it
                assert maybeChoice.isPresent();

                return new ApiResponse(200,maybeChoice.get().toJson());

            } catch (IllegalArgumentException e) {
                body.addProperty("Message","400 invalid opinion type");
                body.addProperty("OpinionType",request.getOpinionType());
                return new ApiResponse(400,body.toString());
            }
        } catch (Exception e){
            body.addProperty("Message", "500 server error");
            body.addProperty("Error", e.getMessage());
            return new ApiResponse(500, body.toString());
        }
    }

    private static boolean validateRequest(OpinionRequest req){
        JsonObject object = req.toJsonObject();
        return object.has("collabId") &&
                       object.has("alternativeId") &&
                       object.has("actionType") &&
                       object.has("opinionType");
    }
}

