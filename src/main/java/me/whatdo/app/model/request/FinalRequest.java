package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FinalRequest {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String alternativeId;
	private String choiceId;

	public FinalRequest(){

	}
	public FinalRequest(String alternativeId, String choiceId){
		this.alternativeId = alternativeId;
		this.choiceId = choiceId;
	}

	public static FinalRequest fromJson(String json) {
		return gson.fromJson(json, FinalRequest.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public String getAlternativeId() {
		return alternativeId;
	}

	public String getChoiceId() {
		return choiceId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = alternativeId;
	}

	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}
}
