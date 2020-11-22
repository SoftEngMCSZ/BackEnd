package me.whatdo.app.entitymodel;

import java.util.List;

public class ChoiceRequest {
	protected final String content;
	protected final List<Alternative> alts;
	protected final int maxCollaborators;

	public ChoiceRequest(String content, List<Alternative> alts, int maxCollaborators) {
		this.content = content;
		this.alts = alts;
		this.maxCollaborators = maxCollaborators;
	}
}
