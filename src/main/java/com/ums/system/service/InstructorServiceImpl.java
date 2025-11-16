package com.ums.system.service;

import com.ums.system.dao.InstructorDAOImpl;
import com.ums.system.model.Instructor;
import com.ums.system.utils.ValidationUtil;
import com.ums.system.utils.PasswordUtil;

import java.sql.Connection;
import java.util.List;

public class InstructorServiceImpl implements InstructorService {
    private final InstructorDAOImpl instructorDAO;

    public InstructorServiceImpl(Connection connection) {
        this.instructorDAO = new InstructorDAOImpl(connection);
    }

    @Override
    public boolean addInstructor(Instructor instructor) {
        String emailError = ValidationUtil.validateEmail(instructor.getEmail());
        if (emailError != null) {
            System.out.println(emailError);
            return false;
        }

        String passwordError = ValidationUtil.validatePassword(instructor.getPassword());
        if (passwordError != null) {
            System.out.println("Password validation failed:");
            System.out.println("  " + passwordError);
            System.out.println("\n" + ValidationUtil.getPasswordRequirements());
            return false;
        }

        Instructor existing = instructorDAO.getByEmail(instructor.getEmail());
        if (existing != null) {
            System.out.println("Instructor with email " + instructor.getEmail() + " already exists.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(instructor.getPassword());
        instructor.setPassword(hashedPassword);

        instructorDAO.insert(instructor);
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
