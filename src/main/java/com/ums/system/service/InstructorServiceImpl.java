package com.ums.system.service;

import com.ums.system.dao.InstructorDAOImpl;
import com.ums.system.model.Instructor;

import java.sql.Connection;
import java.util.List;

public class InstructorServiceImpl implements InstructorService {
    private final InstructorDAOImpl instructorDAO;

    public InstructorServiceImpl(Connection connection) {
        this.instructorDAO = new InstructorDAOImpl(connection);
    }

    @Override
    public boolean addInstructor(Instructor instructor) {
        // Check if instructor with this email already exists before insertion
        Instructor existing = instructorDAO.getByEmail(instructor.getEmail());
        if (existing != null) {
            System.out.println("⚠️ Instructor with email " + instructor.getEmail() + " already exists.");
            return false; // Email already exists, creation failed
        }
        instructorDAO.insert(instructor);
        // Verify the instructor was actually created
        Instructor created = instructorDAO.getByEmail(instructor.getEmail());
        return created != null;
    }

    @Override
    public void updateInstructor(Instructor instructor) {
        instructorDAO.update(instructor);
    }

    @Override
    public void deleteInstructor(int id) {
        instructorDAO.delete(id);
    }

    @Override
    public Instructor getInstructorById(int id) {
        return instructorDAO.getById(id);
    }

    @Override
    public Instructor getInstructorByEmail(String email) {
        return instructorDAO.getByEmail(email);
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorDAO.getAll();
    }
}
