package com.ums.system.service;

import com.ums.system.dao.QuestionDAOImpl;
import com.ums.system.dao.QuizDAO;
import com.ums.system.dao.QuizDAOImpl;
import com.ums.system.model.Quiz;
import com.ums.system.service.QuizService;

import java.sql.Connection;
import java.util.List;

public class QuizServiceImpl implements QuizService {

    private final QuizDAOImpl quizDAO;

    public QuizServiceImpl(Connection connection) {
        this.quizDAO = new QuizDAOImpl(connection,new QuestionDAOImpl(connection));
    }

    @Override
    public void createQuiz(Quiz quiz) {
        quizDAO.insert(quiz);
    }

    @Override
    public void updateQuiz(Quiz quiz) {
        quizDAO.update(quiz);
    }

    @Override
    public void deleteQuiz(int id) {
        quizDAO.delete(id);
    }

    @Override
    public Quiz getQuizById(int id) {
        return quizDAO.getById(id);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizDAO.getAll();
    }

    @Override
    public List<Quiz> getQuizzesByCourseCode(String courseCode) {
        return quizDAO.getByCourseCode(courseCode);
    }
}
