package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class Feedback {
	private final Collaborator author;
	private final Date timestamp;
	private final String content;
	private static final  Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Feedback(Collaborator author, Date timestamp, String content) {
		this.author = author;
		this.timestamp = timestamp;
		this.content = content;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public static Feedback fromJson(String json){
		return gson.fromJson(json, Feedback.class);
	}

	public Collaborator getAuthor() {
		return author;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getContent() {
		return content;
	}
}

