package com.ums.system.dao;

import com.ums.system.dao.QuizDAO;
import com.ums.system.model.Question;
import com.ums.system.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAOImpl implements QuizDAO {

    private final Connection connection;

    public QuizDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Quiz quiz) {
        String quizSql = "INSERT INTO quizzes (title) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(quizSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int quizId = rs.getInt(1);

                String questionSql = "INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement qps = connection.prepareStatement(questionSql)) {
                    for (Question q : quiz.getQuestions()) {
                        qps.setInt(1, quizId);
                        qps.setString(2, q.getText());
                        for (int i = 0; i < 4; i++) {
                            qps.setString(i + 3, q.getOptions().size() > i ? q.getOptions().get(i) : null);
                        }
                        qps.setInt(7, q.getCorrectOptionIndex());
                        qps.addBatch();
                    }
                    qps.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Quiz quiz) {
        String sql = "UPDATE quizzes SET title=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setInt(2, quiz.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM quizzes WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Quiz getById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM quizzes WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        new ArrayList<>()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Quiz> getAll() {
        List<Quiz> quizzes = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM quizzes")) {
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        new ArrayList<>()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }
}
