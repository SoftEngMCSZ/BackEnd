package me.whatdo.app.entitymodel;

public class ChoiceRequest {
	protected final String content;
	protected final Collaborator creator;
	protected final Alternative alt1;
	protected final Alternative alt2;
	protected final int maxCollaborators;

	public ChoiceRequest(String content, Collaborator creator, Alternative alt1, Alternative alt2, int maxCollaborators) {
		this.content = content;
		this.creator = creator;
		this.alt1 = alt1;
		this.alt2 = alt2;
		this.maxCollaborators = maxCollaborators;
	}
}
