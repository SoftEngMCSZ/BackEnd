package me.whatdo.app.db;

public enum Opinion {
	APPROVAL,
	DISAPPROVAL;

	public Opinion invert() {
		switch (this) {
			case APPROVAL: return DISAPPROVAL;
			case DISAPPROVAL: return APPROVAL;
		}
		return null; // Unreachable
	}
}
