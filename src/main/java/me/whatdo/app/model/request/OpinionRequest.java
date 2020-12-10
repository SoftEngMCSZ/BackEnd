package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class OpinionRequest {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String collabId;
	private String alternativeId;
	private String choiceId;
	private String opinionType;
	private String actionType;

	public OpinionRequest(String collabId, String alternativeId, String choiceId, String opinionType, String actionType) {
		this.collabId = collabId;
		this.alternativeId = alternativeId;
		this.choiceId = choiceId;
		this.opinionType = opinionType;
		this.actionType = actionType;
	}

	public OpinionRequest() { }

	public static OpinionRequest fromJson(String json) {
		return gson.fromJson(json, OpinionRequest.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public String getCollabId() {
		return collabId;
	}

	public void setCollabId(String collabId) {
		this.collabId = collabId;
	}

	public String getAlternativeId() {
		return alternativeId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = alternativeId;
	}

	public String getChoiceId() {
		return choiceId;
	}

	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}

	public String getOpinionType() {
		return opinionType;
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


}
