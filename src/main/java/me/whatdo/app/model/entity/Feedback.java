package me.whatdo.app.model.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Feedback {
	private final UUID id;
	private final Collaborator author;
	private final Date timestamp;
	private final String contents;
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Feedback(Collaborator author, Date timestamp, String content) {
		this.id = UUID.randomUUID();
		this.author = author;
		this.timestamp = timestamp;
		this.contents = content;
	}

	public Feedback(UUID id, Collaborator author, Date timestamp, String content) {
		this.id = id;
		this.author = author;
		this.timestamp = timestamp;
		this.contents = content;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public JsonObject toJsonObject(){
		return gson.fromJson(this.toJson(),JsonObject.class);
	}

	public static Feedback fromJson(String json){
		return gson.fromJson(json, Feedback.class);
	}

	public UUID getId() { return id;}

	public Collaborator getAuthor() {
		return author;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getContent() {
		return contents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || o.getClass() != this.getClass()) return false;
		Feedback otherFeedback = (Feedback) o;
		return this.id.equals(otherFeedback.getId());
	}
}
