package com.ums.system.model;

import java.util.Set;

public class Student extends User{
    private int level;
    private String major;
    private Set<Course> courses;
    private int alarm;
    private Department departmentName;

    public Student(String name, String email, String password, Role role, int level, String major, Set<Course> courses, int alarm, Department departmentName) {
        super(name, email, password, role);
        this.level = level;
        this.major = major;
        this.courses = courses;
        this.alarm = alarm;
        this.departmentName = departmentName;
    }

    public int getLevel() {
        return level;
    }

    public String getMajor() {
        return major;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public int getAlarm() {
        return alarm;
    }

    public Department getDepartmentName() {
        return departmentName;
    }
}
