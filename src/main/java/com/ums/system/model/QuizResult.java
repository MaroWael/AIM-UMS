package com.ums.system.model;

import java.util.Map;

public class QuizResult {
    private Student student;
    private Quiz quiz;
    private int score;
    private Map<Question, String> answers;

    public QuizResult(Student student, Quiz quiz, int score, Map<Question, String> answers) {
        this.student = student;
        this.quiz = quiz;
        this.score = score;
        this.answers = answers;
    }

    public Student getStudent() {
        return student;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public int getScore() {
        return score;
    }

    public Map<Question, String> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "student=" + student +
                ", quiz=" + quiz +
                ", score=" + score +
                ", answers=" + answers +
                '}';
    }
}
