package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.UUID;

public class OpinionRequest {
	private UUID collabId;
	private UUID alternativeId;
	private UUID choiceId;
	private String opinionType;
	private String actionType;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public OpinionRequest(UUID collabId, UUID alternativeId, UUID choiceId, String opinionType, String actionType) {
		this.collabId = collabId;
		this.alternativeId = alternativeId;
		this.choiceId = choiceId;
		this.opinionType = opinionType;
		this.actionType = actionType;
	}

	public OpinionRequest() {
	}

	public UUID getCollabId() {
		return collabId;
	}

	public UUID getAlternativeId() {
		return alternativeId;
	}

	public UUID getChoiceId() {
		return choiceId;
	}

	public String getOpinionType() {
		return opinionType;
	}

	public void setCollabId(UUID collabId) {
		this.collabId = collabId;
	}

	public void setAlternativeId(UUID alternativeId) {
		this.alternativeId = alternativeId;
	}

	public void setChoiceId(UUID choiceId) {
		this.choiceId = choiceId;
	}

	public void setOpinionType(String opinionType) {
		this.opinionType = opinionType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public static OpinionRequest fromJson(String json) {
		return gson.fromJson(json, OpinionRequest.class);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(),JsonObject.class);
	}
}
