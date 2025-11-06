package com.ums.system.service;

import com.ums.system.model.Instructor;
import java.util.List;

public interface InstructorService {
    boolean addInstructor(Instructor instructor);
    void updateInstructor(Instructor instructor);
    void deleteInstructor(int id);
    Instructor getInstructorById(int id);
    Instructor getInstructorByEmail(String email);
    List<Instructor> getAllInstructors();
}
