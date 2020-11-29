package me.whatdo.app.db;

import me.whatdo.app.model.entity.Collaborator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OpinionDAO {
	Connection conn;

	private static final String tblName = "opinions";

	public OpinionDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addOpinion(UUID altId, UUID authorId, Opinion opinion) throws Exception {
		try {
			PreparedStatement queryFindExisting = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative_id = ? AND author = ?;");
			queryFindExisting.setObject(1,altId);
			queryFindExisting.setObject(2, authorId);
			ResultSet results = queryFindExisting.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				results.close();
				return false;
			}

			PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (author, alternative_id, opinion) values(?,?,?::opinion);");
			queryAdd.setObject(1, authorId);
			queryAdd.setObject(2,altId);
			queryAdd.setObject(3,opinion.toString());

			queryAdd.execute();
			return true;
		}
		catch (Exception e) {
			throw new Exception("Failed to add " + opinion.toString() + " of author "+ authorId +" for alternative "+altId.toString()+". Error: "+e.getMessage());
		}
	}

	public Optional<Opinion> getOpinion(UUID altId, UUID authorId) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative_id = ? AND author = ?;");
			queryFind.setObject(1,altId);
			queryFind.setObject(2, authorId);
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			if(results.next()) {
				Opinion out = Opinion.valueOf(results.getString("opinion"));
				results.close();
				return Optional.of(out);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get opinion of author " + authorId + " of alternative " + altId + ". Error: "+e.getMessage());
		}
	}

	public List<Collaborator> getAllOpinionsForAlt(UUID altId, Opinion opinion) throws Exception {
		ArrayList<Collaborator> out = new ArrayList<>();

		try {
			CollaboratorDAO collabDao = new CollaboratorDAO();
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative_id = ? AND opinion = ?::opinion;");
			queryFind.setObject(1,altId);
			queryFind.setString(2,opinion.toString());
			ResultSet results = queryFind.executeQuery();
			// Check if a collaborator with the same name is already registered for that choice
			while(results.next()) {
				Optional<Collaborator> author = collabDao.getCollaborator(results.getObject("author",UUID.class));
				assert author.isPresent();
				out.add(author.get());
			}
			results.close();
			return out;
		}
		catch (Exception e) {
			throw new Exception("Failed to get " + opinion.toString().toLowerCase() + "s on alternative "+altId+". Error: " + e.getMessage());
		}
	}

	public boolean deleteOpinion(UUID altId, UUID authorId, Opinion opinion) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE alternative_id = ? AND author = ? AND opinion = ?::opinion;");
			queryDelete.setObject(1,altId);
			queryDelete.setObject(2, authorId);
			queryDelete.setString(3,opinion.toString());
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw new Exception("Failed to delete " + opinion.toString().toLowerCase() + " by author " + authorId + " of alternative " + altId.toString() + ". Error: "+e.getMessage());
		}
	}

	public int deleteAllOpinionsForAlt(UUID altId) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE alternative_id = ?;");
			queryDelete.setObject(1,altId);
			return queryDelete.executeUpdate();
		} catch (Exception e) {
			throw new Exception("Failed to delete opinions for alt " + altId.toString() + ". Error: "+e.getMessage());
		}
	}
}
