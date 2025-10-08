package com.ums.system.dao;

import com.ums.system.dao.QuizResultDAO;
import com.ums.system.model.QuizResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAOImpl implements QuizResultDAO {

    private final Connection connection;

    public QuizResultDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(QuizResult result) {
        String sql = "INSERT INTO quiz_results (student_id, quiz_id, score) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, result.getStudent().getId());
            ps.setInt(2, result.getQuiz().getId());
            ps.setInt(3, result.getScore());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int resultId = rs.getInt(1);
                String ansSql = "INSERT INTO quiz_answers (result_id, question_id, chosen_answer) VALUES (?, ?, ?)";
                try (PreparedStatement aps = connection.prepareStatement(ansSql)) {
                    for (var entry : result.getAnswers().entrySet()) {
                        aps.setInt(1, resultId);
                        aps.setInt(2, entry.getKey().getId());
                        aps.setString(3, entry.getValue());
                        aps.addBatch();
                    }
                    aps.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<QuizResult> getByStudentId(int studentId) {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_results WHERE student_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new QuizResult(null, null, rs.getInt("score"), null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<QuizResult> getByQuizId(int quizId) {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_results WHERE quiz_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new QuizResult(null, null, rs.getInt("score"), null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
