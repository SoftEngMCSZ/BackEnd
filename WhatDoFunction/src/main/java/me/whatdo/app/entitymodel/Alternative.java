package me.whatdo.app.entitymodel;

import java.util.*;

public class Alternative {
	private final UUID id;
	private final String description;
	private final HashMap<Collaborator,Opinion> opinions;
	private final List<Feedback> feedback;

	public Alternative(String description) {
		this.id = UUID.randomUUID();
		this.description = description;
		this.opinions = new HashMap<>();
		this.feedback = new ArrayList<>();
	}

	public boolean addOpinion(Collaborator author, Opinion opinion) {
		this.opinions.put(author,opinion);
		return true;
	}

	public boolean removeOpinion(Collaborator author) {
		return this.opinions.remove(author) != null;
	}

	public boolean addFeedback(Feedback feedback) {
		this.feedback.add(feedback);
		return true;
	}

	public String getDescription() {
		return description;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Alternative that = (Alternative) o;
		return this.id.equals(that.id) &&
					   this.description.equals(that.description) &&
					   this.opinions.equals(that.opinions) &&
					   this.feedback.equals(that.feedback);
	}

	public int hashCode() {
		return Objects.hash(id, description, opinions, feedback);
	}
}
