package me.whatdo.app.db;

import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class FeedbackDAO {

    java.sql.Connection conn;

    final String tblName = "feedback";

    public FeedbackDAO() {
        try {
            conn = DatabaseUtil.connect();
        } catch (Exception e) {
            conn = null;
        }
    }

    public boolean addFeedback(Feedback feedback) throws Exception {
        try {
            PreparedStatement queryAdd = conn.prepareStatement("INSERT INTO " + tblName + " (id, author, alternative, timestamp, content) values(?, ?,?,?,?);");
            queryAdd.setObject(1, feedback.getFeedbackID());
            queryAdd.setString(2, feedback.getAuthor().getName());
            queryAdd.setObject(3, feedback.getAlternativeID());
            queryAdd.setObject(4, new Timestamp(feedback.getTimestamp().getTime()));
            queryAdd.setString(5, feedback.getContent());
            queryAdd.execute();

            return true;
        } catch (Exception e) {
            throw new Exception("Failed to insert feedback: " + e.getMessage());
        }
    }

    public boolean deleteFeedback(UUID alternativeID, UUID feedbackID) throws Exception {
        try {
            PreparedStatement queryDelete = conn.prepareStatement("DELETE FROM " + tblName + " WHERE alternative = ? AND id = ?;");
            queryDelete.setObject(1, alternativeID);
            queryDelete.setObject(2, feedbackID);
            int numAffected = queryDelete.executeUpdate();
            queryDelete.close();

            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to delete feedback: " + e.getMessage());
        }
    }

    public List<Feedback> getAllFeedback(UUID alternativeID) throws Exception {
        try {
            ArrayList<Feedback> feedbackList = new ArrayList<Feedback>();
            PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative = ?;");
            queryFind.setObject(1, alternativeID);
            ResultSet resultSet = queryFind.executeQuery();

            while(resultSet.next()) {
                Feedback feedback = generateFeedback(resultSet);

                // Order feedback by timestamp (most recent to least recent)
                for(int i = 0; i <= resultSet.getFetchSize(); i++) {

                    // If we have reached the end of the list
                    if (i == feedbackList.size()) {
                        feedbackList.add(feedback);
                        break;
                    }

                    // If timestamp is after, insert before
                    if (feedback.getTimestamp().compareTo(feedbackList.get(i).getTimestamp()) < 0) {
                        feedbackList.add(i, feedback);
                    }

                }
            }

            resultSet.close();

            return feedbackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed in getting feedback: " + e.getMessage());
        }
    }

    public Optional<Feedback> getFeedback(UUID alternativeID, UUID feedbackID) throws Exception {
        try {
            PreparedStatement queryFind = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative = ? AND id = ?;");
            queryFind.setObject(1, alternativeID);
            queryFind.setObject(2, feedbackID);
            ResultSet resultSet = queryFind.executeQuery();

            if(resultSet.next()) {
                Feedback feedback = generateFeedback(resultSet);
                resultSet.close();
                return Optional.of(feedback);
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new Exception("Failed to get feedback " + feedbackID + ". Error: "+e.getMessage());
        }
    }

    public Feedback generateFeedback(ResultSet resultSet) throws Exception {
        UUID alternativeID = resultSet.getObject("alternative", UUID.class);
        UUID feedbackID = resultSet.getObject("id", UUID.class);
        Collaborator author = new Collaborator(resultSet.getString("author"));
        String content = resultSet.getString("content");
        Date timestamp = new Date(resultSet.getTimestamp("timestamp").getTime());

        return new Feedback(alternativeID, feedbackID, author, timestamp, content);
    }
}
