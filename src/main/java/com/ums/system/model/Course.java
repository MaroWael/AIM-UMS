package com.ums.system.model;

import java.util.List;

public class Course {
    private String code;
    private String courseName;
    private String level;
    private String Major;
    private String lectureTime;
    private List<Student> students;
    private List<Quiz> quizzes;
    private int InstructorId;
    private int studentCount;
    private int quizCount;

    public Course(String code, String courseName, String level, String major, String lectureTime, List<Student> students, List<Quiz> quizzes, int instructorId) {
        this.code = code;
        this.courseName = courseName;
        this.level = level;
        Major = major;
        this.lectureTime = lectureTime;
        this.students = students;
        this.quizzes = quizzes;
        InstructorId = instructorId;
        this.studentCount = 0;
        this.quizCount = 0;
    }

    public String getCode() {
        return code;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getLevel() {
        return level;
    }

    public String getMajor() {
        return Major;
    }

    public String getLectureTime() {
        return lectureTime;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public int getInstructorId() {
        return InstructorId;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

}
