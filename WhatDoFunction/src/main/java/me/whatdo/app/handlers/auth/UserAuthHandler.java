package me.whatdo.app.handlers.auth;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Collaborator;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class UserAuthHandler {
	public static boolean isUserAuthenticated(String authHeader, UUID choiceId) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		CollaboratorDAO dao = new CollaboratorDAO();

		String[] usernameAndPassword = new String(decoder.decode(authHeader.getBytes())).split(":");
		String username = usernameAndPassword[0];
		String password = usernameAndPassword[1];

		Optional<Collaborator> collab = dao.getCollaborator(choiceId,username);

		if(collab.isPresent()) {
			boolean out = collab.get().verifyPassword(password);
			return out;
		}
		return false;
	}
}
