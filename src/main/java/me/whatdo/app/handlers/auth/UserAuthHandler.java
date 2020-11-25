package me.whatdo.app.handlers.auth;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Collaborator;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class UserAuthHandler {
	public static boolean isUserAuthenticated(String authHeader, UUID choiceId) throws Exception {
		CollaboratorDAO dao = new CollaboratorDAO();

		String[] usernameAndPassword = decode(authHeader).split(":");
		String username = usernameAndPassword[0];
		String password;
		if(usernameAndPassword.length == 2) {
			password = usernameAndPassword[1];
		} else {
			password = "";
		}

		Optional<Collaborator> collab = dao.getCollaborator(choiceId,username);
		return collab.map(collaborator -> collaborator.verifyPassword(password)).orElse(false);
	}

	private static String decode(String encodedStr){
		Base64.Decoder decoder = Base64.getDecoder();
		return new String(decoder.decode(encodedStr));
	}

	public static String encode(String plainStr){
		Base64.Encoder encoder = Base64.getEncoder();
		return new String(encoder.encode(plainStr.getBytes()));
	}
}
