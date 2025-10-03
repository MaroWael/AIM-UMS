package com.ums.system.model;

import java.util.Set;

public class Instructor extends User{
    private Department Department;
    private Set<Course> AssignedCourses;

    public Instructor(String name, String email, String password, Role role, Department department, Set<Course> assignedCourses) {
        super(name, email, password, role);
        Department = department;
        AssignedCourses = assignedCourses;
    }

    public Department getDepartment() {
        return Department;
    }

    public Set<Course> getAssignedCourses() {
        return AssignedCourses;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "Department=" + Department +
                ", AssignedCourses=" + AssignedCourses +
                '}';
    }
}
