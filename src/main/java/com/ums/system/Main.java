package com.ums.system;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.utils.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            AdminService adminService = new AdminServiceImpl(conn);
            InstructorService instructorService = new InstructorServiceImpl(conn);
            StudentService studentService = new StudentServiceImpl(conn);

            Admin admin = new Admin("Islam Ali", "islam@ums.com", "admin123", Role.ADMIN);
            adminService.addAdmin(admin);
            System.out.println("Admin added successfully.");

            Instructor instructor = new Instructor(
                    "Dr. Ahmed Youssef",
                    "ahmed@ums.com",
                    "teach123",
                    Role.INSTRUCTOR,
                    Department.CS,
                    null
            );
            instructorService.addInstructor(instructor);
            System.out.println("Instructor added successfully.");

            Student student = new Student(
                    "Nada Khaled",
                    "nada@ums.com",
                    "stud123",
                    Role.STUDENT,
                    4,
                    "Data Science",
                    null,
                    0,
                    Department.AI,
                    3.8
            );
            studentService.addStudent(student);
            System.out.println("Student added successfully.");

            System.out.println("\nAll Admins:");
            adminService.getAllAdmins().forEach(a ->
                    System.out.println(a.getName() + " - " + a.getEmail())
            );

            System.out.println("\nAll Instructors:");
            instructorService.getAllInstructors().forEach(i ->
                    System.out.println(i.getName() + " - " + i.getDepartment())
            );

            System.out.println("\nAll Students:");
            studentService.getAllStudents().forEach(s ->
                    System.out.println(s.getName() + " - " + s.getMajor() + " (" + s.getDepartmentName() + "), GPA: " + s.getGrade())
            );

            DatabaseConnection.getInstance().closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
