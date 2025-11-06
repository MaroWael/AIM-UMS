package com.ums.system.service;

import com.ums.system.dao.CourseDAOImpl;
import com.ums.system.dao.InstructorDAOImpl;
import com.ums.system.model.Course;

import java.sql.Connection;
import java.util.List;

public class CourseServiceImpl implements CourseService {

    private final CourseDAOImpl courseDAO;
    private final InstructorDAOImpl instructorDAO;

    public CourseServiceImpl(Connection connection) {
        this.courseDAO = new CourseDAOImpl(connection);
        this.instructorDAO = new InstructorDAOImpl(connection);
    }

    @Override
    public boolean addCourse(Course course) {
        // Business logic: Check if course with this code already exists
        if (courseDAO.existsByCode(course.getCode())) {
            System.out.println("⚠️ Course with code " + course.getCode() + " already exists.");
            return false;
        }

        // Business logic: Validate that only instructors can be assigned to courses
        if (!instructorDAO.instructorExistsById(course.getInstructorId())) {
            System.out.println("⚠️ Invalid instructor ID: " + course.getInstructorId() + ". Only instructors can be assigned to courses.");
            return false;
        }

        // Call DAO to insert and return its result
        return courseDAO.insert(course);
    }

    @Override
    public boolean updateCourse(Course course) {
        // Business logic: Validate that only instructors can be assigned to courses
        if (!instructorDAO.instructorExistsById(course.getInstructorId())) {
            System.out.println("⚠️ Invalid instructor ID: " + course.getInstructorId() + ". Only instructors can be assigned to courses.");
            return false;
        }
        courseDAO.update(course);
        return true;
    }

    @Override
    public void deleteCourse(String code) {
        courseDAO.delete(code);
    }

    @Override
    public Course getCourseByCode(String code) {
        return courseDAO.getByCode(code);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.getAll();
    }

    @Override
    public List<Course> getCoursesByInstructorId(int instructorId) {
        return courseDAO.getByInstructorId(instructorId);
    }
}
