package com.ums.system.service;

import com.ums.system.model.Instructor;
import java.util.List;

public interface InstructorService {
    void addInstructor(Instructor instructor);
    void updateInstructor(Instructor instructor);
    void deleteInstructor(int id);
    Instructor getInstructorById(int id);
    List<Instructor> getAllInstructors();
}
