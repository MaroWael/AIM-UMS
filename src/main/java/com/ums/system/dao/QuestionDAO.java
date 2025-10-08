package com.ums.system.dao;

import com.ums.system.model.Question;
import java.util.List;

public interface QuestionDAO {
    void insert(Question question, int quizId);
    void update(Question question);
    void delete(int id);
    List<Question> getByQuizId(int quizId);
}
