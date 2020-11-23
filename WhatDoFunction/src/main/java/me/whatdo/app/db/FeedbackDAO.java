package me.whatdo.app.db;


import me.whatdo.app.entitymodel.Collaborator;
import me.whatdo.app.entitymodel.Feedback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
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

    public boolean addFeedback(Collaborator author, UUID alternativeID, Date timestamp, String content) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative = ?;");
            ps.setObject(1, alternativeID);
            ResultSet resultSet = ps.executeQuery();

            ps = conn.prepareStatement("INSERT INTO " + tblName + " (author, alternative, timestamp, content) values(?,?,?,?);");
            ps.setString(1, author.getName());
            ps.setObject(2, alternativeID);
            ps.setObject(3, new Timestamp(timestamp.getTime()));
            ps.setString(4, content);
            ps.execute();
            return true;
        } catch (Exception e) {
            throw new Exception("Failed to insert feedback: " + e.getMessage());
        }
    }

    public boolean deleteFeedback(Collaborator author, UUID alternativeID, Date timestamp) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tblName + " WHERE name = ? AND altenative = ? AND timestamp = ?;");
            ps.setString(1, author.getName());
            ps.setObject(2, alternativeID);
            ps.setObject(3, new Timestamp(timestamp.getTime()));
            int numAffected = ps.executeUpdate();
            ps.close();

            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete feedback: " + e.getMessage());
        }
    }

    public List<Feedback> getAllFeedback(UUID alternativeID) throws Exception {
        try {
            ArrayList<Feedback> feedbackList = new ArrayList<Feedback>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE alternative = ?;");
            ps.setObject(1, alternativeID);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                Feedback feedback = generateFeedback(resultSet);

                // Order feedback by timestamp (most recent to least recent)
                for(int i = 0; i <= feedbackList.size(); i++) {

                    // If we have reached the end of the list
                    if (i == feedbackList.size()) {
                        feedbackList.add(feedback);
                        break;
                    }

                    // If timestamp is after, insert before
                    if (feedback.getTimestamp().compareTo(feedbackList.get(i).getTimestamp()) > 0) {
                        feedbackList.add(i, feedback);
                    }

                }
            }

            resultSet.close();
            ps.close();

            return feedbackList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed in getting feedback: " + e.getMessage());
        }
    }

    public Feedback generateFeedback(ResultSet resultSet) throws Exception {
        Collaborator author = new Collaborator(resultSet.getString("author"));
        String content = resultSet.getString("content");
        Date timestamp = new Date(resultSet.getTimestamp("timestamp").getTime());

        return new Feedback(author, timestamp, content);
    }
}
