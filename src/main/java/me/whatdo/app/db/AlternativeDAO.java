package me.whatdo.app.db;

import me.whatdo.app.model.entity.Alternative;
import me.whatdo.app.model.entity.Collaborator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
			OpinionDAO opinionDao = new OpinionDAO();

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

			// TODO: Insert any associated feedback

			queryAdd.execute();

			for(Collaborator collab: alt.getApprovals()) {
				opinionDao.addOpinion(alt.getId(),collab.getId(),Opinion.APPROVAL);
			}
			for(Collaborator collab: alt.getDisapprovals()) {
				opinionDao.addOpinion(alt.getId(),collab.getId(),Opinion.DISAPPROVAL);
			}
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
			OpinionDAO opinionDao = new OpinionDAO();
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1,alt.getId());
			// TODO: Cascade delete all associated feedback
			opinionDao.deleteAllOpinionsForAlt(alt.getId());
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();
			return numAffected == 1;
		}
		catch (Exception e) {
			throw new Exception("Failed to delete alternative " + alt.getId() + ". Error: "+e.getMessage());
		}
	}

	public int deleteAllAlternativesInChoice(UUID choiceId) throws Exception {
		try {
			OpinionDAO opinionDao = new OpinionDAO();
			PreparedStatement queryCollectIds = conn.prepareStatement("SELECT id FROM " + tblName + " WHERE choice = ?;");
			queryCollectIds.setObject(1,choiceId);
			ResultSet results = queryCollectIds.executeQuery();

			while(results.next()) {
				// TODO: Cascade delete all associated feedback
				opinionDao.deleteAllOpinionsForAlt(results.getObject("id",UUID.class));
			}

			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE choice = ?;");
			queryDelete.setObject(1,choiceId);
			return queryDelete.executeUpdate();
		} catch (Exception e) {
			throw new Exception("Failed to delete alternatives of choice " + choiceId + ". Error: "+e.getMessage());
		}
	}

	private static Alternative buildAlternative(ResultSet results) throws Exception {
		OpinionDAO opinionDao = new OpinionDAO();
		UUID id = results.getObject("id",UUID.class);
		String description = results.getString("description");
		Set<Collaborator> approvals = new HashSet<>(opinionDao.getAllOpinionsForAlt(id,Opinion.APPROVAL));
		Set<Collaborator> disapprovals = new HashSet<>(opinionDao.getAllOpinionsForAlt(id,Opinion.DISAPPROVAL));
		// TODO: Add all associated feedback
		return new Alternative(id,description,approvals,disapprovals,new ArrayList<>());
	}
}
