package com.ums.system.service;

import com.ums.system.dao.QuizResultDAOImpl;
import com.ums.system.model.QuizResult;
import com.ums.system.service.QuizResultService;

import java.sql.Connection;
import java.util.List;

public class QuizResultServiceImpl implements QuizResultService {

    private final QuizResultDAOImpl quizResultDAO;

    public QuizResultServiceImpl(Connection connection) {
        this.quizResultDAO = new QuizResultDAOImpl(connection);
    }

    @Override
    public void saveResult(QuizResult result) {
        quizResultDAO.insert(result);
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
