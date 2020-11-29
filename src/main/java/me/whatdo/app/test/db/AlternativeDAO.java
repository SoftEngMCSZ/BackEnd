package me.whatdo.app.test.db;

import me.whatdo.app.entitymodel.Alternative;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AlternativeDAO {
	Connection conn;

	private static final String tblName = "alternatives";

	public AlternativeDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addAlternative(UUID choiceId, Alternative alt) throws Exception {
		try {

			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFindExisting.setObject(1,alt.getId());
			ResultSet results = queryFindExisting.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				results.close();
				return false;
			}

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (id,description,choice) values(?,?,?);");
			queryAdd.setObject(1,alt.getId());
			queryAdd.setString(2,alt.getContents());
			queryAdd.setObject(3,choiceId);

			// TODO: Insert any associated feedback & opinions

			queryAdd.execute();
			return true;
		}
		catch (Exception e) {
			throw new Exception("Failed to add alternative "+alt.getId()+". Error: "+e.getMessage());
		}
	}

	public Optional<Alternative> getAlternative(UUID altId) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1,altId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				Alternative out = buildAlternative(results);
				// TODO: Get any associated feedback & opinions
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get alternative " + altId + ". Error: "+e.getMessage());
		}
	}

	public List<Alternative> getAllAlternativesInChoice(UUID choiceId) throws Exception {
		ArrayList<Alternative> out = new ArrayList<>();

		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE choice = ?;");
			queryFind.setObject(1,choiceId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			while(results.next()) {
				// TODO: Find and add relevant feedback & opinions
				out.add(buildAlternative(results));
			}
			results.close();
			return out;
		}
		catch (Exception e) {
			throw new Exception("Failed to get alternatives from choice "+choiceId+". Error: " + e.getMessage());
		}
	}

	public boolean deleteAlternative(Alternative alt) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1,alt.getId());
			// TODO: Cascade delete all associated feedback & opinions
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw new Exception("Failed to delete alternative " + alt.getId() + ". Error: "+e.getMessage());
		}
	}

	private static Alternative buildAlternative(ResultSet results) throws Exception {
		UUID id = results.getObject("id",UUID.class);
		String description = results.getString("description");

		return new Alternative(id,description);
	}
}
