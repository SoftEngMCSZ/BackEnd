package me.whatdo.app.model.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.whatdo.app.model.request.FeedbackRequest;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Feedback {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private final UUID feedbackId, alternativeId, authorId;
	private final Date timestamp;
	private final String contents;

	public Feedback(FeedbackRequest req){
		this(UUID.fromString(req.getAlternativeId()),UUID.fromString(req.getCollaboratorId()), req.getContents());
	}

	public Feedback(UUID altId, UUID authorId, String content) {
		this.feedbackId = UUID.randomUUID();
		this.alternativeId = altId;
		this.authorId = authorId;
		this.contents = content;
		this.timestamp = Date.from(Instant.now());
	}

	public static Feedback fromJson(String json) {
		return gson.fromJson(json, Feedback.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public UUID getFeedbackId() {
		return feedbackId;
	}

	public UUID getAuthor() {
		return authorId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getContent() {
		return contents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.feedbackId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || o.getClass() != this.getClass()) return false;
		Feedback otherFeedback = (Feedback) o;
		return this.feedbackId.equals(otherFeedback.getFeedbackId());
	}
}
