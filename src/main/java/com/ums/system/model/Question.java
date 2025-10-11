package com.ums.system.model;

import java.util.List;

public class Question {
    private int id;
    private String text;
    private List<String> options;
    private int correctOptionIndex;

    public Question(int id, String text, List<String> options, int correctOptionIndex) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }


}
