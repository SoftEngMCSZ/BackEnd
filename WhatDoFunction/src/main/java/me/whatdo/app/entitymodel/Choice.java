package me.whatdo.app.entitymodel;

import java.time.Instant;
import java.util.*;

public class Choice {
	private static final int MAX_ALTERNATIVES = 5;

	private final String id;
	private final String content;
	private final List<Alternative> alternatives;
	private final Set<Collaborator> collaborators;
	private Alternative selectedAlternative;
	private final Date creationTime;
	private Date completionTime;
	private final int maxCollaborators;

	public Choice(String content, Collaborator creator, Alternative alt1, Alternative alt2, int maxCollaborators) {
		this.id = ""; // TODO: Random Choice ID generation
		this.content = content;
		this.alternatives = Arrays.asList(alt1,alt2);
		this.collaborators = new HashSet<>();
		this.collaborators.add(creator);
		this.maxCollaborators = maxCollaborators;
		this.creationTime = Date.from(Instant.now());
		this.selectedAlternative = null;
		this.completionTime = null;
	}

	public boolean addCollaborator(Collaborator c) {
		if(this.collaborators.size() < this.maxCollaborators && !this.collaborators.contains(c)) {
			this.collaborators.add(c);
			return true;
		} else return false;
	}

	public boolean addAlternative(Alternative a) {
		if(this.alternatives.size() < MAX_ALTERNATIVES) {
			this.alternatives.add(a);
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
}
