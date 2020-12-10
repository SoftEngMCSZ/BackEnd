package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FeedbackRequest {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String collaboratorId;
	private String alternativeId;
	private String choiceId;
	private String contents;

	public FeedbackRequest() { }

	public FeedbackRequest(String collabId, String alternativeId, String choiceId, String contents) {
		this.collaboratorId = collabId;
		this.alternativeId = alternativeId;
		this.choiceId = choiceId;
		this.contents = contents;
	}

	public static OpinionRequest fromJson(String json) {
		return gson.fromJson(json, OpinionRequest.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public String getCollaboratorId() {
		return collaboratorId;
	}

	public void setCollaboratorId(String collaboratorId) {
		this.collaboratorId = collaboratorId;
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

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
