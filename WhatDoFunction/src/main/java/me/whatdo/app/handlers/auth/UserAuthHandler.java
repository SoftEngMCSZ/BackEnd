package me.whatdo.app.handlers.auth;

import me.whatdo.app.db.CollaboratorDAO;
import me.whatdo.app.entitymodel.Collaborator;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class UserAuthHandler {
	public static boolean isUserAuthenticated(String authHeader, UUID choiceId) throws Exception {
		System.out.println("Received:"+authHeader);

		CollaboratorDAO dao = new CollaboratorDAO();

		String[] usernameAndPassword = decode(authHeader).split(":");
		String username = usernameAndPassword[0];
		String password = usernameAndPassword[1];

		System.out.println(username+" : "+password);

		Optional<Collaborator> collab = dao.getCollaborator(choiceId,username);

		if(collab.isPresent()) {
			System.out.println("Present");
			boolean out = collab.get().verifyPassword(password);
			return out;
		}
		return false;
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
