package com.ums.system.service;

import com.ums.system.dao.QuizResultDAOImpl;
import com.ums.system.dao.StudentDAOImpl;
import com.ums.system.model.QuizResult;

import java.sql.Connection;
import java.util.List;

public class QuizResultServiceImpl implements QuizResultService {

    private final QuizResultDAOImpl quizResultDAO;
    private final StudentDAOImpl studentDAO;

    public QuizResultServiceImpl(Connection connection) {
        this.quizResultDAO = new QuizResultDAOImpl(connection);
        this.studentDAO = new StudentDAOImpl(connection);
    }

    @Override
    public void saveResult(QuizResult result) {
        quizResultDAO.insert(result);

        int studentId = result.getStudent().getId();
        double averageGrade = quizResultDAO.calculateAverageGrade(studentId);

        studentDAO.updateGrade(studentId, averageGrade);

        System.out.println("Student grade updated to: " + String.format("%.2f", averageGrade) + "%");
    }

    @Override
    public List<QuizResult> getResultsByStudentId(int studentId) {
        return quizResultDAO.getByStudentId(studentId);
    }

    @Override
    public List<QuizResult> getResultsByQuizId(int quizId) {
        return quizResultDAO.getByQuizId(quizId);
    }
}
