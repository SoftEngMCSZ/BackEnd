package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.whatdo.app.model.entity.Alternative;

import java.util.List;

public class CreateChoiceRequest {
	public String question;
	public List<Alternative> alternatives;
	public int maxCollaborators;
	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public CreateChoiceRequest(String question, List<Alternative> alts, int maxCollaborators) {
		this.question = question;
		this.alternatives = alts;
		this.maxCollaborators = maxCollaborators;
	}

	public CreateChoiceRequest(){

	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	public void setMaxCollaborators(int maxCollaborators) {
		this.maxCollaborators = maxCollaborators;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public JsonObject toJsonObject(){
		return gson.fromJson(this.toJson(),JsonObject.class);
	}

	public static CreateChoiceRequest fromJson(String json){
		return gson.fromJson(json, CreateChoiceRequest.class);
	}
}
