package me.whatdo.app.entitymodel;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.*;

public class Alternative {
	private final UUID alternativeId;
	private final String contents;
	private final Set<Collaborator> approvals;
	private final Set<Collaborator> disapprovals;
	private final List<Feedback> feedback;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Alternative(String contents) {
		this.alternativeId = UUID.randomUUID();
		this.contents = contents;
		this.approvals = new HashSet<>();
		this.disapprovals = new HashSet<>();
		this.feedback = new ArrayList<>();
	}

	public Alternative(UUID id, String contents) {
		this.alternativeId = id;
		this.contents = contents;
		this.approvals = new HashSet<>();
		this.disapprovals = new HashSet<>();
		this.feedback = new ArrayList<>();
	}
  
	public String toJson(){
	    return gson.toJson(this);
    }

    public JsonObject toJsonObject(){
	    return gson.fromJson(this.toJson(),JsonObject.class);
    }

    public static Alternative fromJson(String json){
	    return gson.fromJson(json, Alternative.class);
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
		return alternativeId;
	}

	public String getContents() {
		return contents;
	}

	public UUID getID(){
	    return this.alternativeId;
    }

	public boolean equals(Object o) {
		Alternative that = (Alternative) o;
		return this.alternativeId.equals(that.alternativeId);
	}

	public int hashCode() {
		return Objects.hash(alternativeId);
	}
}
