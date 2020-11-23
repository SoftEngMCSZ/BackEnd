package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ChoiceRequest {
	protected final String content;
	protected final List<Alternative> alternatives;
	protected final int maxCollaborators;
	protected static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public ChoiceRequest(String content, List<Alternative> alts, int maxCollaborators) {
		this.content = content;
		this.alternatives = alts;
		this.maxCollaborators = maxCollaborators;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public static ChoiceRequest fromJson(String json){
		return gson.fromJson(json, ChoiceRequest.class);
	}
}
