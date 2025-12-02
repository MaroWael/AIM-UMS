package com.ums.system.controller;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/**
 * TakeQuizController - Handles the quiz-taking interface
 * Provides interactive quiz taking with timer, navigation, and answer tracking
 */
public class TakeQuizController {

    @FXML private Label quizTitleLabel;
    @FXML private Label courseLabel;
    @FXML private Label timerLabel;
    @FXML private Label progressLabel;
    @FXML private Label answeredLabel;
    @FXML private ProgressBar questionProgress;
    @FXML private VBox questionCard;
    @FXML private Label questionNumberLabel;
    @FXML private Label questionTypeLabel;
    @FXML private Label questionTextLabel;
    @FXML private VBox optionsContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;

    // Data
    private Quiz quiz;
    private Student currentStudent;
    private Course course;
    private List<Question> questions;
    private Map<Question, String> answers;
    private int currentQuestionIndex;
    private ToggleGroup optionsGroup;

    // Timer
    private Timeline timeline;
    private int remainingSeconds;
    private static final int QUIZ_TIME_MINUTES = 30;

    // Services
    private QuizResultService quizResultService;
    private CourseService courseService;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        quizResultService = serviceLocator.getQuizResultService();
        courseService = serviceLocator.getCourseService();

        answers = new HashMap<>();
        currentQuestionIndex = 0;
    }

    /**
     * Set up the quiz for taking
     */
    public void setQuizData(Quiz quiz, Student student) {
        this.quiz = quiz;
        this.currentStudent = student;
        this.questions = quiz.getQuestions();

        // Load course information
        try {
            course = courseService.getCourseByCode(quiz.getCourseCode());
        } catch (Exception e) {
            course = null;
        }

        // Initialize UI
        quizTitleLabel.setText(quiz.getTitle());
        if (course != null) {
            courseLabel.setText("Course: " + course.getCode() + " - " + course.getCourseName());
        } else {
            courseLabel.setText("Course: " + quiz.getCourseCode());
        }

        // Initialize timer
        remainingSeconds = QUIZ_TIME_MINUTES * 60;
        startTimer();

        // Load first question
        loadQuestion(0);
    }

    /**
     * Start the quiz timer
     */
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;

            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));

            // Change timer color when time is running out
            if (remainingSeconds <= 300) { // 5 minutes
                timerLabel.setStyle("-fx-text-fill: #ff4444; -fx-background-color: rgba(255, 68, 68, 0.2); " +
                                   "-fx-background-radius: 8; -fx-padding: 8 15 8 15; -fx-font-size: 18px; -fx-font-weight: bold;");
            }

            if (remainingSeconds <= 0) {
                timeline.stop();
                autoSubmitQuiz();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Load a specific question
     */
    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question question = questions.get(index);

        // Update question info
        questionNumberLabel.setText("Question " + (index + 1));
        questionTextLabel.setText(question.getText());

        // Update progress
        progressLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionProgress.setProgress((double) (index + 1) / questions.size());

        // Update answered count
        int answeredCount = answers.size();
        answeredLabel.setText("Answered: " + answeredCount + "/" + questions.size());

        // Clear previous options
        optionsContainer.getChildren().clear();
        optionsGroup = new ToggleGroup();

        // Create option buttons
        List<String> options = question.getOptions();
        String[] optionLetters = {"A", "B", "C", "D", "E", "F"};

        for (int i = 0; i < options.size(); i++) {
            String optionText = options.get(i);
            String optionLetter = optionLetters[i];

            // Create option container
            HBox optionBox = new HBox(15);
            optionBox.getStyleClass().add("option-box");
            optionBox.setPadding(new Insets(15, 20, 15, 20));

            // Create radio button
            RadioButton radio = new RadioButton();
            radio.setToggleGroup(optionsGroup);
            radio.getStyleClass().add("option-radio");
            radio.setUserData(optionText);

            // Create option label with letter
            Label letterLabel = new Label(optionLetter + ".");
            letterLabel.getStyleClass().add("option-letter");
            letterLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #667eea; -fx-min-width: 25px;");

            // Create option text label
            Label textLabel = new Label(optionText);
            textLabel.getStyleClass().add("option-text");
            textLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50;");
            textLabel.setWrapText(true);
            textLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(textLabel, javafx.scene.layout.Priority.ALWAYS);

            optionBox.getChildren().addAll(radio, letterLabel, textLabel);

            // Add click handler to the entire box
            optionBox.setOnMouseClicked(event -> {
                radio.setSelected(true);
                updateOptionStyles();
            });

            // Restore previous answer if exists
            String previousAnswer = answers.get(question);
            if (previousAnswer != null && previousAnswer.equals(optionText)) {
                radio.setSelected(true);
            }

            optionsContainer.getChildren().add(optionBox);
        }

        // Update option styles initially
        updateOptionStyles();

        // Save answer when selection changes
        optionsGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String selectedAnswer = (String) newVal.getUserData();
                answers.put(question, selectedAnswer);
                updateAnsweredCount();
            }
        });

        // Update navigation buttons
        updateNavigationButtons();
    }

    /**
     * Update the visual style of option boxes based on selection
     */
    private void updateOptionStyles() {
        for (int i = 0; i < optionsContainer.getChildren().size(); i++) {
            HBox optionBox = (HBox) optionsContainer.getChildren().get(i);
            RadioButton radio = (RadioButton) optionBox.getChildren().get(0);

            if (radio.isSelected()) {
                optionBox.getStyleClass().remove("option-box");
                optionBox.getStyleClass().add("option-box-selected");
            } else {
                optionBox.getStyleClass().remove("option-box-selected");
                if (!optionBox.getStyleClass().contains("option-box")) {
                    optionBox.getStyleClass().add("option-box");
                }
            }
        }
    }

    /**
     * Update answered count label
     */
    private void updateAnsweredCount() {
        int answeredCount = answers.size();
        answeredLabel.setText("Answered: " + answeredCount + "/" + questions.size());
    }

    /**
     * Update navigation buttons visibility and state
     */
    private void updateNavigationButtons() {
        // Previous button
        previousButton.setDisable(currentQuestionIndex == 0);

        // Next/Submit button
        boolean isLastQuestion = currentQuestionIndex == questions.size() - 1;
        nextButton.setVisible(!isLastQuestion);
        submitButton.setVisible(isLastQuestion);
    }

    /**
     * Handle Previous button
     */
    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            loadQuestion(currentQuestionIndex - 1);
        }
    }

    /**
     * Handle Next button
     */
    @FXML
    private void handleNext() {
        if (currentQuestionIndex < questions.size() - 1) {
            loadQuestion(currentQuestionIndex + 1);
        }
    }

    /**
     * Handle Cancel button
     */
    @FXML
    private void handleCancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Quiz");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Do you want to cancel this quiz? Your progress will be lost.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (timeline != null) {
                timeline.stop();
            }
            closeQuizWindow();
        }
    }

    /**
     * Handle Submit button
     */
    @FXML
    private void handleSubmit() {
        // Check if all questions are answered
        int answeredCount = answers.size();
        int totalQuestions = questions.size();
        int unansweredCount = totalQuestions - answeredCount;

        if (unansweredCount > 0) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Submit Quiz");
            confirm.setHeaderText("Unanswered Questions");
            confirm.setContentText("You have " + unansweredCount + " unanswered question(s).\n" +
                                  "Do you want to submit anyway?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Submit Quiz");
            confirm.setHeaderText("Submit Quiz");
            confirm.setContentText("Are you sure you want to submit your quiz?\n" +
                                  "You have answered all " + totalQuestions + " questions.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        submitQuiz();
    }

    /**
     * Submit the quiz and calculate score
     */
    private void submitQuiz() {
        if (timeline != null) {
            timeline.stop();
        }

        // Calculate score
        int correctAnswers = 0;
        for (Question question : questions) {
            String studentAnswer = answers.get(question);
            if (studentAnswer != null) {
                int correctIndex = question.getCorrectOptionIndex();
                String correctAnswer = question.getOptions().get(correctIndex);
                if (studentAnswer.equals(correctAnswer)) {
                    correctAnswers++;
                }
            }
        }

        // Create and save quiz result
        QuizResult result = new QuizResult(currentStudent, quiz, correctAnswers, answers);
        try {
            quizResultService.saveResult(result);
        } catch (Exception e) {
            showError("Error saving quiz result: " + e.getMessage());
            return;
        }

        // Show result summary
        showResultSummary(correctAnswers, questions.size());
    }

    /**
     * Auto-submit quiz when time runs out
     */
    private void autoSubmitQuiz() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Time's Up!");
        alert.setHeaderText("Quiz Time Expired");
        alert.setContentText("Your quiz will be automatically submitted.");
        alert.showAndWait();

        submitQuiz();
    }

    /**
     * Show result summary dialog
     */
    private void showResultSummary(int correctAnswers, int totalQuestions) {
        int percentage = (totalQuestions > 0) ? (correctAnswers * 100 / totalQuestions) : 0;

        Alert result = new Alert(Alert.AlertType.INFORMATION);
        result.setTitle("Quiz Completed");
        result.setHeaderText("Quiz Submitted Successfully!");
        result.setContentText(
            "Quiz: " + quiz.getTitle() + "\n" +
            "Course: " + (course != null ? course.getCourseName() : quiz.getCourseCode()) + "\n\n" +
            "Correct Answers: " + correctAnswers + " / " + totalQuestions + "\n" +
            "Score: " + percentage + "%\n\n" +
            "Your results have been saved."
        );

        result.showAndWait();
        closeQuizWindow();
    }

    /**
     * Close the quiz window
     */
    private void closeQuizWindow() {
        Stage stage = (Stage) quizTitleLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
