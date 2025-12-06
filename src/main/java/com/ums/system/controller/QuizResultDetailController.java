package com.ums.system.controller;

import com.ums.system.model.Course;
import com.ums.system.model.Question;
import com.ums.system.model.Quiz;
import com.ums.system.model.QuizResult;
import com.ums.system.model.Student;
import com.ums.system.service.CourseService;
import com.ums.system.util.ServiceLocator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class QuizResultDetailController {

    @FXML private Label quizTitleLabel;
    @FXML private Label courseLabel;
    @FXML private Label studentLabel;
    @FXML private Label dateLabel;
    @FXML private Label percentageLabel;
    @FXML private Label correctAnswersLabel;
    @FXML private Label gradeLabel;
    @FXML private ProgressBar scoreProgressBar;
    @FXML private VBox questionsContainer;

    private QuizResult quizResult;
    private Student student;
    private CourseService courseService;

    @FXML
    public void initialize() {
        courseService = ServiceLocator.getInstance().getCourseService();
    }

    public void setQuizResult(QuizResult result, Student student) {
        this.quizResult = result;
        this.student = student;

        questionsContainer.getChildren().clear();

        populateData();
    }

    private void populateData() {
        Quiz quiz = quizResult.getQuiz();

        quizTitleLabel.setText(quiz != null ? quiz.getTitle() : "N/A");

        if (quiz != null) {
            try {
                Course course = courseService.getCourseByCode(quiz.getCourseCode());
                courseLabel.setText(course != null ? course.getCourseName() + " (" + quiz.getCourseCode() + ")" : quiz.getCourseCode());
            } catch (Exception e) {
                courseLabel.setText(quiz.getCourseCode());
            }
        } else {
            courseLabel.setText("N/A");
        }

        studentLabel.setText(student.getName() + " (ID: " + student.getId() + ")");

        dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        int rawScore = quizResult.getScore();
        int totalQuestions = quiz != null && quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        int percentage = (totalQuestions > 0) ? (rawScore * 100 / totalQuestions) : 0;

        percentageLabel.setText(percentage + "%");
        correctAnswersLabel.setText(rawScore + " out of " + totalQuestions);
        scoreProgressBar.setProgress(percentage / 100.0);

        String grade = getGradeString(percentage);
        gradeLabel.setText(grade);
        setGradeColor(percentage);

        if (quiz != null && quiz.getQuestions() != null) {
            populateQuestions(quiz.getQuestions(), quizResult.getAnswers());
        }
    }

    private void populateQuestions(List<Question> questions, Map<Question, String> studentAnswers) {
        questionsContainer.getChildren().clear();

        int totalQuestions = questions.size();

        for (int i = 0; i < totalQuestions; i++) {
            Question question = questions.get(i);
            String studentAnswer = (studentAnswers != null) ? studentAnswers.get(question) : null;
            VBox questionCard = createQuestionCard(i + 1, totalQuestions, question, studentAnswer);
            questionsContainer.getChildren().add(questionCard);
        }
    }

    private VBox createQuestionCard(int questionNumber, int totalQuestions, Question question, String studentAnswer) {
        VBox card = new VBox(10);
        card.getStyleClass().add("question-card");
        card.setPadding(new Insets(0));

        String correctAnswer = null;
        List<String> options = question.getOptions();
        int correctIndex = question.getCorrectOptionIndex();
        if (options != null && correctIndex >= 0 && correctIndex < options.size()) {
            correctAnswer = options.get(correctIndex);
        }

        boolean isCorrect = studentAnswer != null && correctAnswer != null &&
                           studentAnswer.trim().equals(correctAnswer.trim());
        boolean isUnanswered = studentAnswer == null || studentAnswer.trim().isEmpty();

        if (isCorrect) {
            card.getStyleClass().add("question-card-correct");
        } else if (isUnanswered) {
            card.getStyleClass().add("question-card-unanswered");
        } else {
            card.getStyleClass().add("question-card-incorrect");
        }

        HBox header = new HBox(10);
        header.getStyleClass().add("question-header");
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label questionNumberLabel = new Label("Question " + questionNumber + " of " + totalQuestions);
        questionNumberLabel.getStyleClass().add("question-number");
        questionNumberLabel.setFont(Font.font("System Bold", 14));

        header.getChildren().add(questionNumberLabel);

        VBox body = new VBox(15);
        body.setPadding(new Insets(20));

        Label questionTextLabel = new Label(question.getText());
        questionTextLabel.getStyleClass().add("question-text");
        questionTextLabel.setWrapText(true);
        questionTextLabel.setFont(Font.font(15));
        body.getChildren().add(questionTextLabel);

        if (options != null && !options.isEmpty()) {
            VBox optionsBox = new VBox(10);

            for (int j = 0; j < options.size(); j++) {
                String option = options.get(j);
                if (option != null && !option.trim().isEmpty()) {
                    HBox optionBox = createOptionBox((char)('A' + j), option,
                                                     studentAnswer, correctAnswer);
                    optionsBox.getChildren().add(optionBox);
                }
            }

            body.getChildren().add(optionsBox);
        }

        if (!isUnanswered) {
            HBox resultBox = new HBox(10);
            resultBox.setAlignment(Pos.CENTER_LEFT);
            resultBox.setPadding(new Insets(15, 15, 15, 15));

            if (isCorrect) {
                resultBox.getStyleClass().add("result-correct");
                Label icon = new Label("✓");
                icon.getStyleClass().add("result-icon");
                icon.setStyle("-fx-text-fill: #28a745;");
                icon.setFont(Font.font(18));
                Label text = new Label("CORRECT! Well done!");
                text.getStyleClass().add("result-text");
                text.setStyle("-fx-text-fill: #28a745;");
                text.setFont(Font.font("System Bold", 14));
                resultBox.getChildren().addAll(icon, text);
            } else {
                resultBox.getStyleClass().add("result-incorrect");
                Label icon = new Label("✗");
                icon.getStyleClass().add("result-icon");
                icon.setStyle("-fx-text-fill: #dc3545;");
                icon.setFont(Font.font(18));
                Label text = new Label("INCORRECT - Review this topic");
                text.getStyleClass().add("result-text");
                text.setStyle("-fx-text-fill: #dc3545;");
                text.setFont(Font.font("System Bold", 14));
                resultBox.getChildren().addAll(icon, text);
            }

            body.getChildren().add(resultBox);
        }

        card.getChildren().addAll(header, body);
        return card;
    }

    private HBox createOptionBox(char letter, String optionText, String studentAnswer, String correctAnswer) {
        HBox box = new HBox(10);
        box.getStyleClass().add("option-box");
        box.setPadding(new Insets(12, 15, 12, 15));
        box.setAlignment(Pos.CENTER_LEFT);

        if (correctAnswer != null && optionText.trim().equals(correctAnswer.trim())) {
            box.getStyleClass().add("option-correct");
        } else if (studentAnswer != null && optionText.trim().equals(studentAnswer.trim()) &&
                   (correctAnswer == null || !optionText.trim().equals(correctAnswer.trim()))) {
            box.getStyleClass().add("option-incorrect");
        }

        Label letterLabel = new Label(letter + ")");
        letterLabel.getStyleClass().add("option-letter");
        letterLabel.setFont(Font.font("System Bold", 14));
        letterLabel.setMinWidth(30);

        Label textLabel = new Label(optionText);
        textLabel.getStyleClass().add("option-text");
        textLabel.setWrapText(true);
        textLabel.setFont(Font.font(14));

        box.getChildren().addAll(letterLabel, textLabel);
        return box;
    }

    private String getGradeString(int percentage) {
        if (percentage >= 90) return "A+ (Excellent!) 🌟🌟🌟";
        else if (percentage >= 85) return "A (Excellent!) 🌟🌟";
        else if (percentage >= 80) return "A- (Very Good!) 🌟";
        else if (percentage >= 75) return "B+ (Good) ⭐";
        else if (percentage >= 70) return "B (Good) 👍";
        else if (percentage >= 65) return "C+ (Satisfactory) ✓";
        else if (percentage >= 60) return "C (Pass) ✓";
        else return "F (Needs Improvement) 📖";
    }

    private void setGradeColor(int percentage) {
        if (percentage >= 80) {
            gradeLabel.setStyle("-fx-text-fill: #28a745;");
        } else if (percentage >= 60) {
            gradeLabel.setStyle("-fx-text-fill: #ffc107;");
        } else {
            gradeLabel.setStyle("-fx-text-fill: #dc3545;");
        }
    }

    @FXML
    private void handlePrint() {
        System.out.println("Print functionality would be implemented here");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) quizTitleLabel.getScene().getWindow();
        stage.close();
    }
}
