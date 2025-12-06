package com.ums.system.util;

import com.ums.system.dao.*;
import com.ums.system.service.*;
import com.ums.system.utils.DatabaseConnection;
import com.ums.system.utils.ReportGenerator;

import java.sql.Connection;

public class ServiceLocator {
    
    private static ServiceLocator instance;

    private Connection connection;

    private AdminService adminService;
    private InstructorService instructorService;
    private CourseService courseService;
    private StudentService studentService;
    private QuizService quizService;
    private QuizResultService quizResultService;

    private EnrollmentDAO enrollmentDAO;
    private QuestionDAO questionDAO;

    private ReportGenerator reportGenerator;

    private ServiceLocator() {
        initializeServices();
    }


    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    private void initializeServices() {
        try {

            connection = DatabaseConnection.getInstance().getConnection();
            
            if (connection == null) {
                throw new RuntimeException("Failed to establish database connection");
            }

            adminService = new AdminServiceImpl(connection);
            instructorService = new InstructorServiceImpl(connection);
            courseService = new CourseServiceImpl(connection);
            studentService = new StudentServiceImpl(connection);
            quizService = new QuizServiceImpl(connection);
            quizResultService = new QuizResultServiceImpl(connection);

            enrollmentDAO = new EnrollmentDAOImpl(connection);
            questionDAO = new QuestionDAOImpl(connection);

            reportGenerator = new ReportGenerator(enrollmentDAO, quizResultService);
            
            System.out.println("ServiceLocator: All services initialized successfully");
            
        } catch (Exception e) {
            System.err.println("ServiceLocator: Error initializing services - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize services", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public InstructorService getInstructorService() {
        return instructorService;
    }
    

    public CourseService getCourseService() {
        return courseService;
    }
    

    public StudentService getStudentService() {
        return studentService;
    }

    public QuizService getQuizService() {
        return quizService;
    }

    public QuizResultService getQuizResultService() {
        return quizResultService;
    }
    

    public EnrollmentDAO getEnrollmentDAO() {
        return enrollmentDAO;
    }

    public QuestionDAO getQuestionDAO() {
        return questionDAO;
    }
    

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }
    

    public void shutdown() {
        try {
            if (connection != null) {
                DatabaseConnection.getInstance().closeConnection();
            }
            System.out.println("ServiceLocator: Resources cleaned up successfully");
        } catch (Exception e) {
            System.err.println("ServiceLocator: Error during shutdown - " + e.getMessage());
        }
    }
}

