package me.whatdo.app.db;

import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Choice;
import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.entity.CompactedChoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * A Database Access Object used for manipulating Choices
 */
public class ChoiceDAO {
	private static final String tblName = "choices";
	Connection conn;

	/**
	 * Constructs a new ChoiceDAO
	 */
	public ChoiceDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	/**
	 * Adds a new Choice to the database
	 *
	 * @param c The Choice to be added
	 * @return true if the Choice was added successfully, false if the choice is already
	 * in the database
	 * @throws Exception If the database is inaccessible for any reason, or if the Choice data
	 *                   is incomplete
	 */
	public boolean addChoice(Choice c) throws Exception {
		try {

			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFindExisting.setObject(1, c.getId());
			ResultSet results = queryFindExisting.executeQuery();
			// Check if a choice with the same id is already added
			if (results.next()) {
				results.close();
				return false;
			}

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (id,question,creation_time,max_collaborators) values(?,?,?,?);");
			queryAdd.setObject(1, c.getId());
			queryAdd.setString(2, c.getQuestion());
			queryAdd.setObject(3, Timestamp.from(c.getCreationTime().toInstant()));
			queryAdd.setInt(4, c.getMaxCollaborators());

			queryAdd.execute();

			AlternativeDAO altDao = new AlternativeDAO();
			for (Alternative alt : c.getAlternatives()) {
				altDao.addAlternative(c.getId(), alt);
			}

			CollaboratorDAO collabDao = new CollaboratorDAO();
			for (Collaborator collab : c.getCollaborators()) {
				collabDao.addCollaborator(c.getId(), collab);
			}


			return true;
		} catch (Exception e) {
			throw new Exception("Failed to add choice " + c.getId() + ". Error: " + e.getMessage());
		}
	}

	/**
	 * Attempts to get an Choice from the database
	 *
	 * @param choiceId the ID of the Choice to retrieve
	 * @return Either the requested Choice, or Optional.empty() if the Choice could not be
	 * found
	 * @throws Exception If the database is inaccessible
	 */
	public Optional<Choice> getChoice(UUID choiceId) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1, choiceId);
			ResultSet results = queryFind.executeQuery();
			if (results.next()) {
				Choice out = buildChoice(results);
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get choice " + choiceId + ". Error: " + e.getMessage());
		}
	}

	/**
	 * Finalize a choice by choosing a selected alternative
	 *
	 * @param choiceId The ID of the choice to finalize
	 * @param altId    The ID of the selected alternative
	 * @return true if the choice could be finalized, false if it is already finalized
	 * @throws Exception If the database is inaccessible
	 */
	public boolean finalizeChoice(UUID choiceId, UUID altId) throws Exception {
		try {

			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFindExisting.setObject(1, choiceId);
			ResultSet results = queryFindExisting.executeQuery();
			if (results.next()) {
				if (results.getObject("selected_alternative", UUID.class) != null) return false;

				PreparedStatement queryFinalize = conn.prepareStatement("UPDATE " + tblName + " SET selected_alternative = ?, completion_time = ? where id = ?");
				queryFinalize.setObject(1, altId);
				queryFinalize.setObject(2, Timestamp.from(Instant.now()));
				queryFinalize.setObject(3, choiceId);
				queryFinalize.execute();
				return true;
			}

			return false;

		} catch (Exception e) {
			throw new Exception("Failed to finalize choice " + choiceId + ". Error: " + e.getMessage());
		}
	}

	/**
	 * Retrieves a compacted report of all Choices in the database
	 *
	 * @return A List of all Choices in compacted form (containing ID, question, creation timestamp, and completion status)
	 * @throws Exception If the database is inaccessible
	 */
	public List<CompactedChoice> getAllChoices() throws Exception {
		ArrayList<CompactedChoice> out = new ArrayList<>();

		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " ORDER BY creation_time DESC;");
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			while (results.next()) {
				out.add(buildCompactedChoice(results));
			}
			results.close();
			return out;
		} catch (Exception e) {
			throw new Exception("Failed to get all choices. Error: " + e.getMessage());
		}
	}

	/**
	 * Deletes a given Choice from the database
	 *
	 * @param c the Choice to be deleted
	 * @return true if the Choice was deleted, false otherwise
	 * @throws Exception If the database is inaccessible
	 */
	public boolean deleteChoice(Choice c) throws Exception {
		try {
			CollaboratorDAO collabDao = new CollaboratorDAO();
			AlternativeDAO altDao = new AlternativeDAO();

			collabDao.deleteAllCollaboratorsOfChoice(c.getId());
			altDao.deleteAllAlternativesInChoice(c.getId());

			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1, c.getId());
			int numAffected = queryDelete.executeUpdate();

			queryDelete.close();
			return numAffected == 1;
		} catch (Exception e) {
			throw new Exception("Failed to delete choice " + c.getId() + ". Error: " + e.getMessage());
		}
	}

	/**
	 * Deletes all Choices older than the given Date
	 *
	 * @param d the date cutoff for Choice deletion
	 * @return the number of deleted Choices
	 * @throws Exception If the database is inaccessible
	 */
	public int deleteChoicesOlderThan(Date d) throws Exception {
		try {
			CollaboratorDAO collabDao = new CollaboratorDAO();
			AlternativeDAO altDao = new AlternativeDAO();

			PreparedStatement queryFindAll = conn.prepareStatement("SELECT id FROM " + tblName + " WHERE creation_time < ?;");
			queryFindAll.setObject(1, new Timestamp(d.getTime()));
			ResultSet results = queryFindAll.executeQuery();

			while (results.next()) {
				altDao.deleteAllAlternativesInChoice(results.getObject("id", UUID.class));
				collabDao.deleteAllCollaboratorsOfChoice(results.getObject("id", UUID.class));
			}
			queryFindAll.close();

			PreparedStatement queryDeleteAll = conn.prepareStatement("DELETE FROM " + tblName + " WHERE creation_time < ?;");
			queryDeleteAll.setObject(1, new Timestamp(d.getTime()));
			return queryDeleteAll.executeUpdate();

		} catch (Exception e) {
			throw new Exception("Failed to delete choices older than " + d + ". Error: " + e.getMessage());
		}
	}

	private static Choice buildChoice(ResultSet results) throws Exception {
		AlternativeDAO altDao = new AlternativeDAO();
		CollaboratorDAO collabDao = new CollaboratorDAO();

		UUID id = results.getObject("id", UUID.class);
		String question = results.getString("question");
		Optional<UUID> selectedAltId = Optional.ofNullable(results.getObject("selected_alternative", UUID.class));
		Optional<Alternative> selectedAlternative = Optional.empty();
		if (selectedAltId.isPresent()) {
			selectedAlternative = altDao.getAlternative(selectedAltId.get());
		}
		Date creationTime = Date.from(results.getObject("creation_time", Timestamp.class).toInstant());
		Optional<Date> completionTime = Optional.ofNullable(results.getObject("completion_time", Timestamp.class))
												.map(ts -> Date.from(ts.toInstant()));
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

	private static CompactedChoice buildCompactedChoice(ResultSet results) throws Exception {
		UUID id = results.getObject("id", UUID.class);
		String question = results.getString("question");
		Date creationTime = Date.from(results.getObject("creation_time", Timestamp.class).toInstant());
		Optional<Date> completionTime = Optional.ofNullable(results.getObject("completion_time", Timestamp.class))
												.map(ts -> Date.from(ts.toInstant()));

		return new CompactedChoice(id, question, creationTime, completionTime.isPresent());
	}
}
