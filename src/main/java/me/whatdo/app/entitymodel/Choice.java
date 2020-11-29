package me.whatdo.app.entitymodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.*;

public class Choice {
	private static final int MAX_ALTERNATIVES = 5;
	private static final int MIN_ALTERNATIVES = 2;

	private final UUID id;
	private final String question;
	private final List<Alternative> alternatives;
	private final Set<Collaborator> collaborators;
	private Alternative selectedAlternative;
	private final Date creationTime;
	private Date completionTime;
	private final int maxCollaborators;
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Choice(ChoiceRequest choiceRequest) {
		this(
				choiceRequest.question,
				choiceRequest.alternatives,
				choiceRequest.maxCollaborators
		);
	}

	public Choice(String question, List<Alternative> alts, int maxCollaborators) {
		this.id = UUID.randomUUID();
		this.question = question;
		this.alternatives = alts;
		this.collaborators = new HashSet<>();
		this.maxCollaborators = maxCollaborators;
		this.creationTime = Date.from(Instant.now());
		this.selectedAlternative = null;
		this.completionTime = null;
	}

	public Choice(
			UUID id,
			String question,
			List<Alternative> alternatives,
			Set<Collaborator> collaborators,
			Optional<Alternative> selectedAlternative,
			Date creationTime,
			Optional<Date> completionTime,
			int maxCollaborators) {
		this.id = id;
		this.question = question;
		this.alternatives = alternatives;
		this.collaborators = collaborators;
		this.selectedAlternative = selectedAlternative.orElse(null);
		this.creationTime = creationTime;
		this.completionTime = completionTime.orElse(null);
		this.maxCollaborators = maxCollaborators;
	}

	public UUID getId() {
		return this.id;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	public String getQuestion() {
		return this.question;
	}

	public List<Alternative> getAlternatives() {
		return this.alternatives;
	}

	public Set<Collaborator> getCollaborators() {
		return this.collaborators;
	}

	public String toJson(){
		return gson.toJson(this);
	}

	public JsonObject toJsonObject(){
		return gson.fromJson(this.toJson(),JsonObject.class);
	}

	public static Optional<Choice> fromJson(String json){
		Choice out = gson.fromJson(json, Choice.class);
		if(
				out.alternatives.size() < MIN_ALTERNATIVES ||
				out.alternatives.size() > MAX_ALTERNATIVES ||
				out.collaborators.size() > out.maxCollaborators
		) {
			return Optional.empty();
		} else {
			return Optional.of(out);
		}
	}

	public boolean addCollaborator(Collaborator c) {
		if(this.collaborators.size() < this.maxCollaborators && !this.collaborators.contains(c)) {
			this.collaborators.add(c);
			return true;
		} else return false;
	}

	public boolean hasCollaborator(Collaborator c) {
		return this.collaborators.contains(c);
	}

	public boolean selectAlternative(Alternative alt) {
		if(this.alternatives.contains(alt)) {
			this.selectedAlternative = alt;
			this.completionTime = Date.from(Instant.now());
			return true;
		} else return false;
	}

	public Optional<Alternative> getSelectedAlternative() {
		return Optional.ofNullable(this.selectedAlternative);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Choice choice = (Choice) o;
		return id.equals(choice.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}
}
