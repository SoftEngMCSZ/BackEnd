package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class AdminRequest {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public AdminRequest() { }

	public JsonObject toJsonObject() {
		return new Gson().fromJson(gson.toJson(this), JsonObject.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}
}
