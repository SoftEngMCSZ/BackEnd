package me.whatdo.app.model.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

public class CompactedChoice {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	UUID id;
	Date creationTime;
	boolean isCompleted;

	public CompactedChoice(UUID id, Date creationTime, boolean isCompleted) {
		this.id = id;
		this.creationTime = creationTime;
		this.isCompleted = isCompleted;
	}

	public static Collaborator fromJson(String json) {
		return gson.fromJson(json, Collaborator.class);
	}

	public UUID getId() {
		return this.id;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}
}
