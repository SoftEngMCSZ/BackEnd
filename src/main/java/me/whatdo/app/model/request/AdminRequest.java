package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class AdminRequest {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	// Using java.lang.Float instead of float because null is a real option
	private Double daysAgo;

	public AdminRequest() { }

	public AdminRequest(double d) {
		this.daysAgo = d;
	}

	public double getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(double d) {
		this.daysAgo = d;
	}

	public JsonObject toJsonObject() {
		return new Gson().fromJson(gson.toJson(this), JsonObject.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}
}
