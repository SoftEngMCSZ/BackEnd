package me.whatdo.app.db;

import me.whatdo.app.model.entity.Collaborator;

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

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (name,choice,password,id) values(?,?,?,?);");
			queryAdd.setString(1,c.getName());
			queryAdd.setObject(2,choiceId);
			queryAdd.setString(3,c.getPassword());
			queryAdd.setObject(4,c.getId());

			queryAdd.execute();
			return true;
		}
		catch (Exception e) {
			throw new Exception("Failed to add collaborator "+c.getName()+". Error: "+e.getMessage());
		}
	}

	public Optional<Collaborator> getCollaborator(UUID collabId) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1,collabId);
			ResultSet results = queryFind.executeQuery();
			if(results.next()) {
				Collaborator out = buildCollaborator(results);
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get collaborator " + collabId + ". Error: "+e.getMessage());
		}
	}

	public Optional<Collaborator> getCollaborator(UUID choiceId, String name) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE choice = ? AND name = ?;");
			queryFind.setObject(1,choiceId);
			queryFind.setString(2,name);
			ResultSet results = queryFind.executeQuery();
			if(results.next()) {
				Collaborator out = buildCollaborator(results);
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get collaborator " + name + " of " + choiceId.toString() + ". Error: "+e.getMessage());
		}
	}

	public List<Collaborator> getAllCollaboratorsInChoice(UUID choiceId) throws Exception {
		ArrayList<Collaborator> out = new ArrayList<>();

		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE choice = ?;");
			queryFind.setObject(1,choiceId);
			ResultSet results = queryFind.executeQuery();
			while(results.next()) {
				out.add(buildCollaborator(results));
			}
			results.close();
			return out;
		}
		catch (Exception e) {
			throw new Exception("Failed to get collaborators from choice "+choiceId+". Error: " + e.getMessage());
		}
	}

	public boolean deleteCollaborator(UUID collabId) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1,collabId);
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw new Exception("Failed to delete collaborator " + collabId + ". Error: "+e.getMessage());
		}
	}

	public int deleteAllCollaboratorsOfChoice(UUID choiceId) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE choice = ?;");
			queryDelete.setObject(1,choiceId);
			return queryDelete.executeUpdate();
		} catch (Exception e) {
			throw new Exception("Failed to delete collaborators of choice " + choiceId + ". Error: "+e.getMessage());
		}
	}

	private static Collaborator buildCollaborator(ResultSet results) throws Exception {
		UUID id = results.getObject("id",UUID.class);
		String name = results.getString("name");
		String password = results.getString("password");
		if(password != null) {
			return new Collaborator(id, name, password);
		} else {
			return new Collaborator(id, name);
		}
	}
}
