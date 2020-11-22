package me.whatdo.app.entitymodel;

import java.util.Date;

public class Feedback {
	private final Collaborator author;
	private final Date timestamp;
	private final String content;

	public Feedback(Collaborator author, Date timestamp, String content) {
		this.author = author;
		this.timestamp = timestamp;
		this.content = content;
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

