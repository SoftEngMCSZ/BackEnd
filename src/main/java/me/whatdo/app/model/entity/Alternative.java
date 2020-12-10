package me.whatdo.app.model.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.*;

public class Alternative {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private UUID alternativeId;
	private String contents;
	private Set<Collaborator> approvals;4
	private Set<Collaborator> disapprovals;
	private List<Feedback> feedback;

	public Alternative(String description) {
		this.alternativeId = UUID.randomUUID();
		this.contents = description;
		this.approvals = new HashSet<>();
		this.disapprovals = new HashSet<>();
		this.feedback = new ArrayList<>();
	}

	public Alternative() {
	}

	public Alternative(UUID id, String description, Set<Collaborator> approvals,
					   Set<Collaborator> disapprovals, List<Feedback> feedback) {
		this.alternativeId = id;
		this.contents = description;
		this.approvals = approvals;
		this.disapprovals = disapprovals;
		this.feedback = feedback;
	}

	public static Alternative fromJson(String json) {
		return gson.fromJson(json, Alternative.class);
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
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

	public Set<Collaborator> getApprovals() {
		return approvals;
	}

	public void setApprovals(Set<Collaborator> approvals) {
		this.approvals = approvals;
	}

	public Set<Collaborator> getDisapprovals() {
		return disapprovals;
	}

	public void setDisapprovals(Set<Collaborator> disapprovals) {
		this.disapprovals = disapprovals;
	}

	public List<Feedback> getFeedback() {
		return feedback;
	}

	public boolean addFeedback(Feedback feedback) {
		return this.feedback.add(feedback);
	}

	public void setFeedback(List<Feedback> feedback){
		this.feedback = feedback;
	}

	public String getAlternativeId(){
		return alternativeId.toString();
	}

	public UUID getId() {
		return alternativeId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = UUID.fromString(alternativeId);
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Alternative that = (Alternative) o;
		return this.alternativeId.equals(that.alternativeId);
	}

	public int hashCode() {
		return Objects.hash(alternativeId);
	}
}
