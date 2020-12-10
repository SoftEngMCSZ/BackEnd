package me.whatdo.app.db;

import me.whatdo.app.model.entity.Collaborator;
import me.whatdo.app.model.entity.Feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class FeedbackDAO {

	private static final String tblName = "feedback";
	Connection conn;

	public FeedbackDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addFeedback(UUID altId, Feedback feedback) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1, feedback.getFeedbackId());
			ResultSet resultSet = queryFind.executeQuery();

			// Check if feedback with the same id already exists
			if (resultSet.next()) {
				resultSet.close();
				return false;
			} else {
				resultSet.close();
				PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (id, author, alternative, timestamp, content) values(?, ?,?,?,?);");
				queryAdd.setObject(1, feedback.getFeedbackId());
				queryAdd.setObject(2, feedback.getAuthor());
				queryAdd.setObject(3, altId);
				queryAdd.setObject(4, new Timestamp(feedback.getTimestamp().getTime()));
				queryAdd.setString(5, feedback.getContent());
				queryAdd.execute();

				return true;
			}

		} catch (Exception e) {
			throw new Exception("Failed to insert feedback: " + e.getMessage());
		}
	}

	public boolean deleteFeedback(UUID feedbackID) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE id = ?;");
			queryDelete.setObject(1, feedbackID);
			int numAffected = queryDelete.executeUpdate();
			queryDelete.close();

			return (numAffected == 1);
		} catch (Exception e) {
			throw new Exception("Failed to delete feedback: " + e.getMessage());
		}
	}

	public List<Feedback> getAllFeedback(UUID alternativeID) throws Exception {
		try {
			ArrayList<Feedback> feedbackList = new ArrayList<>();
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative = ? ORDER BY timestamp;");
			queryFind.setObject(1, alternativeID);
			ResultSet resultSet = queryFind.executeQuery();

			while (resultSet.next()) {
				Feedback feedback = generateFeedback(resultSet);

				feedbackList.add(feedback);
			}

			resultSet.close();

			return feedbackList;
		} catch (Exception e) {
			throw new Exception("Failed getting all feedback on alternative " + alternativeID.toString() + "Error: " + e.getMessage());
		}
	}

	public Optional<Feedback> getFeedback(UUID feedbackID) throws Exception {
		try {
			PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE id = ?;");
			queryFind.setObject(1, feedbackID);
			ResultSet resultSet = queryFind.executeQuery();

			if (resultSet.next()) {
				Feedback feedback = generateFeedback(resultSet);
				resultSet.close();
				return Optional.of(feedback);
			}

			return Optional.empty();
		} catch (Exception e) {
			throw new Exception("Failed to get feedback " + feedbackID + ". Error: " + e.getMessage());
		}
	}

	public int deleteAllFeedbackForAlternative(UUID altId) throws Exception {
		try {
			PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE alternative = ?;");
			queryDelete.setObject(1, altId);
			return queryDelete.executeUpdate();
		} catch (Exception e) {
			throw new Exception("Failed to delete feedback of alternative " + altId + ". Error: " + e.getMessage());
		}
	}

	private static Feedback generateFeedback(ResultSet resultSet) throws Exception {
		UUID id = resultSet.getObject("id", UUID.class);
		String content = resultSet.getString("content");

		return new Feedback(id, resultSet.getObject("author", UUID.class), content);
	}
}
