package com.ums.system.service;

import com.ums.system.dao.CourseDAOImpl;
import com.ums.system.dao.QuestionDAOImpl;
import com.ums.system.dao.QuizDAO;
import com.ums.system.dao.QuizDAOImpl;
import com.ums.system.model.Course;
import com.ums.system.model.Quiz;
import com.ums.system.service.QuizService;

import java.sql.Connection;
import java.util.List;

public class QuizServiceImpl implements QuizService {

    private final QuizDAOImpl quizDAO;
    private final CourseDAOImpl courseDAO;

    public QuizServiceImpl(Connection connection) {
        this.quizDAO = new QuizDAOImpl(connection, new QuestionDAOImpl(connection));
        this.courseDAO = new CourseDAOImpl(connection);
    }

    @Override
    public boolean createQuiz(Quiz quiz, int instructorId) {
        Course course = courseDAO.getByCode(quiz.getCourseCode());

        if (course == null) {
            System.out.println("Course with code " + quiz.getCourseCode() + " does not exist.");
            return false;
        }

        if (course.getInstructorId() != instructorId) {
            System.out.println("You can only create quizzes for courses assigned to you.");
            return false;
        }

        quizDAO.insert(quiz);
        return true;
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

    @Override
    public List<Quiz> getQuizzesByInstructor(int instructorId) {
        return quizDAO.getByInstructorId(instructorId);
    }
}
