package com.ums.system.dao;

import com.ums.system.model.Question;
import com.ums.system.model.Quiz;
import com.ums.system.model.QuizResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

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

            try (ResultSet rs = ps.getGeneratedKeys()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<QuizResult> getByStudentId(int studentId) {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT qr.*, q.title, q.course_code FROM quiz_results qr " +
                     "JOIN quizzes q ON qr.quiz_id = q.id WHERE qr.student_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int resultId = rs.getInt("id");
                    int quizId = rs.getInt("quiz_id");

                    // Load actual questions from database
                    List<Question> questions = loadQuestionsByQuizId(quizId);

                    // Load student's answers from database
                    Map<Question, String> answers = loadAnswersByResultId(resultId, questions);

                    Quiz quiz = new Quiz(
                        quizId,
                        rs.getString("title"),
                        rs.getString("course_code"),
                        questions
                    );
                    list.add(new QuizResult(null, quiz, rs.getInt("score"), answers));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Load all questions for a quiz from the database
     */
    private List<Question> loadQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY id";
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

    /**
     * Load student's answers for a quiz result from the database
     */
    private Map<Question, String> loadAnswersByResultId(int resultId, List<Question> questions) {
        Map<Question, String> answers = new HashMap<>();
        String sql = "SELECT question_id, chosen_answer FROM quiz_answers WHERE result_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, resultId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("question_id");
                    String chosenAnswer = rs.getString("chosen_answer");

                    // Find the matching question object
                    for (Question q : questions) {
                        if (q.getId() == questionId) {
                            answers.put(q, chosenAnswer);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    private List<Question> createEmptyQuestionList(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT COUNT(*) as count FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    for (int i = 0; i < count; i++) {
                        questions.add(new Question(0, "", new ArrayList<>(), 0));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    @Override
    public List<QuizResult> getByQuizId(int quizId) {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT qr.*, q.title, q.course_code, " +
                     "u.id as student_id, u.name as student_name, u.email as student_email, " +
                     "st.level as student_level, st.major as student_major, st.department as student_department " +
                     "FROM quiz_results qr " +
                     "JOIN quizzes q ON qr.quiz_id = q.id " +
                     "JOIN students st ON qr.student_id = st.user_id " +
                     "JOIN users u ON st.user_id = u.id " +
                     "WHERE qr.quiz_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int resultId = rs.getInt("id");

                    // Load actual questions from database
                    List<Question> questions = loadQuestionsByQuizId(quizId);

                    // Load student's answers from database
                    Map<Question, String> answers = loadAnswersByResultId(resultId, questions);

                    // Create Quiz object
                    Quiz quiz = new Quiz(
                        quizId,
                        rs.getString("title"),
                        rs.getString("course_code"),
                        questions
                    );

                    // Create Student object using StudentDAOImpl to get proper Department enum
                    StudentDAOImpl studentDAO = new StudentDAOImpl(connection);
                    com.ums.system.model.Student student = studentDAO.getById(rs.getInt("student_id"));

                    QuizResult result = new QuizResult(student, quiz, rs.getInt("score"), answers);
                    list.add(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public double calculateAverageGrade(int studentId) {
        String sql = """
            SELECT
                AVG(
                    (CAST(qr.score AS DECIMAL(10,2)) / 
                    (SELECT COUNT(*) FROM questions WHERE quiz_id = qr.quiz_id)) * 100
                ) as average_grade
            FROM quiz_results qr
            WHERE qr.student_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("average_grade");
                    return rs.wasNull() ? 0.0 : avg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
