package me.whatdo.app.entitymodel;

import me.whatdo.app.db.Opinion;

import java.util.*;

public class Alternative {
	private final UUID id;
	private final String description;
	private final Set<Collaborator> approvals;
	private final Set<Collaborator> disapprovals;
	private final List<Feedback> feedback;

	public Alternative(String description) {
		this.id = UUID.randomUUID();
		this.description = description;
		this.approvals = new HashSet<>();
		this.disapprovals = new HashSet<>();
		this.feedback = new ArrayList<>();
	}

	public boolean addApproval(Collaborator author) {
		this.approvals.add(author);
		return true;
	}

	public boolean removeApproval(Collaborator author) {
		return this.approvals.remove(author);
	}

	public boolean addDisapproval(Collaborator author) {
		this.disapprovals.add(author);
		return true;
	}

	public boolean removeDisapproval(Collaborator author) {
		return this.disapprovals.remove(author);
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
					   this.approvals.equals(that.approvals) &&
					   this.disapprovals.equals(that.approvals) &&
					   this.feedback.equals(that.feedback);
	}

	public int hashCode() {
		return Objects.hash(id, description, approvals, disapprovals, feedback);
	}
}
