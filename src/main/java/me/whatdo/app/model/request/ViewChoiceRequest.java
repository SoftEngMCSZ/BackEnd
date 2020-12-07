package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ViewChoiceRequest {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String choiceID;
	private String authentication;

	public ViewChoiceRequest() { }

	public ViewChoiceRequest(String id, String auth) {
		this.choiceID = id;
		this.authentication = auth;
	}

	public void setChoiceID(String choiceID) {
		this.choiceID = choiceID;
	}

	public String getID() {
		return choiceID;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}
}
