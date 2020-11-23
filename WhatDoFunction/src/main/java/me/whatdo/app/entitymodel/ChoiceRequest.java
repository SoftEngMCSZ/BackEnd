package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

public class ChoiceRequest {
	protected final String question;
	protected final List<Alternative> alternatives;
	protected final int maxCollaborators;
	protected static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public ChoiceRequest(String question, List<Alternative> alts, int maxCollaborators) {
		this.question = question;
		this.alternatives = alts;
		this.maxCollaborators = maxCollaborators;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public JsonObject toJsonObject(){
		return gson.fromJson(this.toJson(),JsonObject.class);
	}

	public static ChoiceRequest fromJson(String json){
		return gson.fromJson(json, ChoiceRequest.class);
	}
}
