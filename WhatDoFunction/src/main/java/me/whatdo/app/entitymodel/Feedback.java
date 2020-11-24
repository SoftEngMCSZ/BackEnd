package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Feedback {
	private final UUID feedbackID;
	private final UUID alternativeID;
	private final Collaborator author;
	private final Date timestamp;
	private final String contents;
	private static final  Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Feedback(UUID parentID, Collaborator author, Date timestamp, String content) {
        this.alternativeID = parentID;
		this.feedbackID = UUID.randomUUID();
		this.author = author;
		this.timestamp = timestamp;
		this.contents = content;
	}

	public Feedback(UUID parentID, UUID feedbackID, Collaborator author, Date timestamp, String content) {
		this.alternativeID = parentID;
		this.feedbackID = feedbackID;
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

	public UUID getFeedbackID() { return feedbackID;}

	public UUID getAlternativeID() { return alternativeID; }

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
		return Objects.hash(this.feedbackID, this.alternativeID);
	}

	@Override
	public boolean equals(Object o) {
		Feedback otherFeedback = (Feedback) o;
		return this.feedbackID.equals(otherFeedback.getFeedbackID()) && this.alternativeID.equals(otherFeedback.getAlternativeID());
	}
}

