package com.ums.system.dao;

import com.ums.system.model.QuizResult;
import java.util.List;

public interface QuizResultDAO {
    void insert(QuizResult result);
    List<QuizResult> getByStudentId(int studentId);
    List<QuizResult> getByQuizId(int quizId);
}
