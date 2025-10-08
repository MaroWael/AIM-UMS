package com.ums.system.dao;

import com.ums.system.dao.QuestionDAO;
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
            for (int i = 0; i < 4; i++) {
                ps.setString(i + 3, q.getOptions().size() > i ? q.getOptions().get(i) : null);
            }
            ps.setInt(7, q.getCorrectOptionIndex());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Question q) {
        String sql = "UPDATE questions SET text=?, option1=?, option2=?, option3=?, option4=?, correct_option_index=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, q.getText());
            for (int i = 0; i < 4; i++) {
                ps.setString(i + 2, q.getOptions().size() > i ? q.getOptions().get(i) : null);
            }
            ps.setInt(6, q.getCorrectOptionIndex());
            ps.setInt(7, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM questions WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Question> getByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM questions WHERE quiz_id=?")) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
