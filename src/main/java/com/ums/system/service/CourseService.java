package com.ums.system.service;

import com.ums.system.model.Course;
import java.util.List;

public interface CourseService {
    boolean addCourse(Course course);
    boolean updateCourse(Course course);
    void deleteCourse(String code);
    Course getCourseByCode(String code);
    List<Course> getAllCourses();
    List<Course> getCoursesByInstructorId(int instructorId);
}
