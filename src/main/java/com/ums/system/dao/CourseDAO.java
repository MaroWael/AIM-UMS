package com.ums.system.dao;

import com.ums.system.model.Course;
import java.util.List;

public interface CourseDAO {
    void insert(Course course);
    void update(Course course);
    void delete(String code);
    Course getByCode(String code);
    List<Course> getAll();
}
