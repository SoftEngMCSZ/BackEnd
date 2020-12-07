package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.whatdo.app.model.entity.Alternative;

import java.util.List;

public class CreateChoiceRequest {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private String question;
	private List<Alternative> alternatives;
	private int maxCollaborators;

	public CreateChoiceRequest(String question, List<Alternative> alts, int maxCollaborators) {
		this.question = question;
		this.alternatives = alts;
		this.maxCollaborators = maxCollaborators;
	}

	public CreateChoiceRequest() { }

	public static CreateChoiceRequest fromJson(String json) {
		return gson.fromJson(json, CreateChoiceRequest.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	public int getMaxCollaborators() {
		return maxCollaborators;
	}

	public void setMaxCollaborators(int maxCollaborators) {
		this.maxCollaborators = maxCollaborators;
	}
}
