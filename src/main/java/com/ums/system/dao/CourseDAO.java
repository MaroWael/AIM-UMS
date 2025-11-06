package com.ums.system.dao;

import com.ums.system.model.Course;
import java.util.List;

public interface CourseDAO {
    boolean insert(Course course);
    void update(Course course);
    void delete(String code);
    Course getByCode(String code);
    List<Course> getAll();
    List<Course> getByInstructorId(int instructorId);
    boolean existsByCode(String code);
}
