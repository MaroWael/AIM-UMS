package com.ums.system.model;

import java.util.Set;

public class Instructor extends User{
    private Department Department;
    private Set<Course> AssignedCourses;

    public Instructor(int id, String name, String email, String password, Role role, Department department) {
        super(id, name, email, password, role);
        Department = department;
    }

    public Department getDepartment() {
        return Department;
    }

    public Set<Course> getAssignedCourses() {
        return AssignedCourses;
    }

}
