package com.ums.system.dao;

import com.ums.system.model.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDAOImpl implements QuestionDAO {

    private final Connection connection;

    public QuestionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Question q, int quizId) {
        String sql = "INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setString(2, q.getText());
            ps.setString(3, q.getOptions().size() > 0 ? q.getOptions().get(0) : null);
            ps.setString(4, q.getOptions().size() > 1 ? q.getOptions().get(1) : null);
            ps.setString(5, q.getOptions().size() > 2 ? q.getOptions().get(2) : null);
            ps.setString(6, q.getOptions().size() > 3 ? q.getOptions().get(3) : null);
            ps.setInt(7, q.getCorrectOptionIndex());
            ps.executeUpdate();
            System.out.println("Question inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Question q) {
        if (!existsById(q.getId())) {
            System.out.println("No question found with id " + q.getId());
            return;
        }

        String sql = "UPDATE questions SET text=?, option1=?, option2=?, option3=?, option4=?, correct_option_index=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, q.getText());
            ps.setString(2, q.getOptions().size() > 0 ? q.getOptions().get(0) : null);
            ps.setString(3, q.getOptions().size() > 1 ? q.getOptions().get(1) : null);
            ps.setString(4, q.getOptions().size() > 2 ? q.getOptions().get(2) : null);
            ps.setString(5, q.getOptions().size() > 3 ? q.getOptions().get(3) : null);
            ps.setInt(6, q.getCorrectOptionIndex());
            ps.setInt(7, q.getId());
            ps.executeUpdate();
            System.out.println("Question updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        if (!existsById(id)) {
            System.out.println("No question found with id " + id);
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM questions WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Question deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Question> getByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getString("text"),
                            Arrays.asList(
                                    rs.getString("option1"),
                                    rs.getString("option2"),
                                    rs.getString("option3"),
                                    rs.getString("option4")
                            ),
                            rs.getInt("correct_option_index")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM questions WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
