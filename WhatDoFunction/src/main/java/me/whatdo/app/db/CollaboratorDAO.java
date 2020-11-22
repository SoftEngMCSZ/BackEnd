package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Collaborator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CollaboratorDAO {
	Connection conn;

	private static final String tblName = "collaborators";

	public CollaboratorDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addCollaborator(UUID choiceId, Collaborator c) throws Exception {
		try {
			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE name = ? AND choice = ?;");
			queryFindExisting.setString(1,c.getName());
			queryFindExisting.setObject(2,choiceId);
			ResultSet results = queryFindExisting.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				results.close();
				return false;
			}

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (name,password) values(?,?);");
			queryAdd.setString(1,c.getName());
			queryAdd.setString(2,c.getPassword());

			return queryAdd.execute();
		}
		catch (Exception e) {
			throw e;
		}
	}

	public Optional<Collaborator> getCollaborator(UUID choiceId, String name) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE name = ? AND choice = ?;");
			queryFind.setString(1,name);
			queryFind.setObject(2,choiceId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				Collaborator out = buildCollaborator(results);
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get collaborator " + name + " of choice " + choiceId);
		}
	}

	public List<Collaborator> getAllCollaboratorsInChoice(UUID choiceId) throws Exception {
		ArrayList<Collaborator> out = new ArrayList<>();

		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE name = ? AND choice = ?;");
			queryFind.setObject(1,choiceId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			while(results.next()) {
				out.add(buildCollaborator(results));
			}
			results.close();
			return out;
		}
		catch (Exception e) {
			throw e;
		}
	}

	public boolean deleteCollaborator(UUID choiceId, Collaborator c) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM" + tblName + " WHERE name = ? AND choice = ?;");
			queryDelete.setString(1,c.getName());
			queryDelete.setObject(2,choiceId);
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw e;
		}
	}

	private static Collaborator buildCollaborator(ResultSet results) throws Exception {
		String name = results.getString("name");
		String password = results.getString("password");
		if(password != null) {
			return new Collaborator(name,password);
		} else {
			return new Collaborator(name);
		}
	}
}
