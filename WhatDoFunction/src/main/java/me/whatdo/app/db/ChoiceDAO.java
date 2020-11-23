package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Alternative;
import me.whatdo.app.entitymodel.Choice;
import me.whatdo.app.entitymodel.Collaborator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class ChoiceDAO {
	Connection conn;

	private static final String tblName = "choices";

	public ChoiceDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addChoice(Choice c) throws Exception {
		try {

			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFindExisting.setObject(1,c.getId());
			ResultSet results = queryFindExisting.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				results.close();
				return false;
			}

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (id,question,creation_time) values(?,?,?);");
			queryAdd.setObject(1,c.getId());
			queryAdd.setString(2,c.getQuestion());
			queryAdd.setObject(3, Timestamp.from(c.getCreationTime().toInstant()));

			AlternativeDAO altDao = new AlternativeDAO();
			for(Alternative alt: c.getAlternatives()) {
				altDao.addAlternative(c.getId(),alt);
			}

			CollaboratorDAO collabDao = new CollaboratorDAO();
			for(Collaborator collab: c.getCollaborators()) {
				collabDao.addCollaborator(c.getId(),collab);
			}

			queryAdd.execute();
			return true;
		}
		catch (Exception e) {
			throw new Exception("Failed to add alternative "+c.getId()+". Error: "+e.getMessage());
		}
	}

	public Optional<Choice> getChoice(UUID choiceId) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1,choiceId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				Choice out = buildChoice(results);
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get alternative " + choiceId + ". Error: "+e.getMessage());
		}
	}

	public List<Choice> getAllChoices() throws Exception {
		ArrayList<Choice> out = new ArrayList<>();

		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + ";");
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			while(results.next()) {
				out.add(buildChoice(results));
			}
			results.close();
			return out;
		}
		catch (Exception e) {
			throw new Exception("Failed to get all choices. Error: " + e.getMessage());
		}
	}

	public boolean deleteChoice(Choice c) throws Exception {
		try {
			CollaboratorDAO collabDao = new CollaboratorDAO();
			AlternativeDAO altDao = new AlternativeDAO();

			for(Collaborator collab: c.getCollaborators()) {
				collabDao.deleteCollaborator(c.getId(),collab);
			}

			for(Alternative alt: c.getAlternatives()) {
				altDao.deleteAlternative(alt);
			}

			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1,c.getId());
			int numAffected = queryDelete.executeUpdate();

			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw new Exception("Failed to delete alternative " + c.getId() + ". Error: "+e.getMessage());
		}
	}

	private static Choice buildChoice(ResultSet results) throws Exception {
		AlternativeDAO altDao = new AlternativeDAO();
		CollaboratorDAO collabDao = new CollaboratorDAO();

		UUID id = results.getObject("id",UUID.class);
		String question = results.getString("question");
		Optional<UUID> selectedAltId = Optional.ofNullable(results.getObject("selected_alternative",UUID.class));
		Optional<Alternative> selectedAlternative = Optional.empty();
		if(selectedAltId.isPresent()) {
			selectedAlternative = altDao.getAlternative(selectedAltId.get());
		}
		Date creationTime = Date.from(results.getObject("creation_time",Timestamp.class).toInstant());
		Optional<Date> completionTime = Optional.ofNullable(results.getObject("completion_time",Timestamp.class))
												.map(ts-> Date.from(ts.toInstant()));
		int maxCollaborators = results.getInt("max_collaborators");

		List<Alternative> alternatives = altDao.getAllAlternativesInChoice(id);
		Set<Collaborator> collaborators = new HashSet<>(collabDao.getAllCollaboratorsInChoice(id));

		return new Choice(
				id,
				question,
				alternatives,
				collaborators,
				selectedAlternative,
				creationTime,
				completionTime,
				maxCollaborators
		);
	}
}
