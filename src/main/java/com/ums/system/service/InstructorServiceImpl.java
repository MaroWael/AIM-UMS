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
    public void addInstructor(Instructor instructor) {
        instructorDAO.insert(instructor);
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
    public List<Instructor> getAllInstructors() {
        return instructorDAO.getAll();
    }
}
