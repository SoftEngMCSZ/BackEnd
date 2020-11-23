package me.whatdo.app.entitymodel;

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

	public Alternative(UUID id, String description) {
		this.id = id;
		this.description = description;
		this.approvals = new HashSet<>();
		this.disapprovals = new HashSet<>();
		this.feedback = new ArrayList<>();
	}

	public boolean addApproval(Collaborator author) {
		this.disapprovals.remove(author);
		return this.approvals.add(author);
	}

	public boolean removeApproval(Collaborator author) {
		return this.approvals.remove(author);
	}

	public boolean addDisapproval(Collaborator author) {
		this.approvals.remove(author);
		return this.disapprovals.add(author);
	}

	public boolean removeDisapproval(Collaborator author) {
		return this.disapprovals.remove(author);
	}

	public boolean addFeedback(Feedback feedback) {
		return this.feedback.add(feedback);
	}

	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public boolean equals(Object o) {
		Alternative that = (Alternative) o;
		return this.id.equals(that.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}
}
