package com.ums.system.model;

import java.util.List;

public class Quiz {

    private int id;
    private String title;
    private String courseCode;
    private List<Question> questions;

    public Quiz(int id, String title, String courseCode, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.courseCode = courseCode;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public List<Question> getQuestions() {
        return questions;
    }

}
