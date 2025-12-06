package com.ums.system.controller;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {

    @FXML private TextField quizTitleField;
    @FXML private ComboBox<Course> courseCombo;
    @FXML private Spinner<Integer> numQuestionsSpinner;
    @FXML private ScrollPane questionsScrollPane;
    @FXML private VBox questionsContainer;

    private Instructor currentInstructor;
    private CourseService courseService;
    private QuizService quizService;
    private boolean quizCreated = false;
    private List<QuestionForm> questionForms = new ArrayList<>();

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        quizService = serviceLocator.getQuizService();

        setupCourseCombo();

        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        numQuestionsSpinner.setValueFactory(valueFactory);
    }

    private void setupCourseCombo() {
        courseCombo.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCode() + " - " + course.getCourseName());
                }
            }
        });

        courseCombo.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCode() + " - " + course.getCourseName());
                }
            }
        });
    }

    public void setInstructor(Instructor instructor) {
        this.currentInstructor = instructor;
        loadInstructorCourses();
    }

    private void loadInstructorCourses() {
        try {
            List<Course> courses = courseService.getCoursesByInstructorId(currentInstructor.getId());
            if (courses.isEmpty()) {
                showError("You have no assigned courses. Cannot create quiz.");
                handleCancel();
                return;
            }
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            courseCombo.setItems(coursesList);

            if (!courses.isEmpty()) {
                courseCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateQuestions() {
        int numQuestions = numQuestionsSpinner.getValue();
        questionsContainer.getChildren().clear();
        questionForms.clear();

        for (int i = 0; i < numQuestions; i++) {
            QuestionForm questionForm = new QuestionForm(i + 1);
            questionForms.add(questionForm);
            questionsContainer.getChildren().add(questionForm.getContainer());
        }

        showInfo("Generated " + numQuestions + " question forms. Please fill them out.");
    }

    @FXML
    private void handleCreateQuiz() {
        String title = quizTitleField.getText().trim();
        Course selectedCourse = courseCombo.getValue();

        if (title.isEmpty()) {
            showError("Please enter a quiz title!");
            return;
        }

        if (selectedCourse == null) {
            showError("Please select a course!");
            return;
        }

        if (questionForms.isEmpty()) {
            showError("Please generate question forms first!");
            return;
        }

        List<Question> questions = new ArrayList<>();
        for (QuestionForm form : questionForms) {
            try {
                Question question = form.getQuestion();
                questions.add(question);
            } catch (IllegalArgumentException e) {
                showError("Question " + form.getQuestionNumber() + ": " + e.getMessage());
                return;
            }
        }

        try {
            Quiz quiz = new Quiz(0, title, selectedCourse.getCode(), questions);
            boolean success = quizService.createQuiz(quiz, currentInstructor.getId());

            if (success) {
                quizCreated = true;
                showInfo("Quiz created successfully with " + questions.size() + " questions!");
                closeDialog();
            } else {
                showError("Failed to create quiz. Please try again.");
            }
        } catch (Exception e) {
            showError("Error creating quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    public boolean isQuizCreated() {
        return quizCreated;
    }

    private void closeDialog() {
        Stage stage = (Stage) quizTitleField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class QuestionForm {
        private int questionNumber;
        private VBox container;
        private TextArea questionTextArea;
        private TextField[] choiceFields;
        private ToggleGroup correctAnswerGroup;
        private RadioButton[] correctAnswerRadios;

        public QuestionForm(int questionNumber) {
            this.questionNumber = questionNumber;
            createForm();
        }

        private void createForm() {
            container = new VBox(10);
            container.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; " +
                             "-fx-border-radius: 5; -fx-background-color: #f9f9f9; " +
                             "-fx-padding: 15;");

            Label headerLabel = new Label("Question " + questionNumber);
            headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label questionLabel = new Label("Question Text:");
            questionTextArea = new TextArea();
            questionTextArea.setPromptText("Enter the question text...");
            questionTextArea.setPrefRowCount(2);
            questionTextArea.setWrapText(true);

            Label choicesLabel = new Label("Choices:");
            choicesLabel.setStyle("-fx-font-weight: bold;");

            choiceFields = new TextField[4];
            correctAnswerRadios = new RadioButton[4];
            correctAnswerGroup = new ToggleGroup();

            VBox choicesBox = new VBox(8);
            for (int i = 0; i < 4; i++) {
                HBox choiceBox = new HBox(10);
                choiceBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label choiceNumLabel = new Label((i + 1) + ".");
                choiceNumLabel.setMinWidth(20);

                choiceFields[i] = new TextField();
                choiceFields[i].setPromptText("Choice " + (i + 1));
                HBox.setHgrow(choiceFields[i], javafx.scene.layout.Priority.ALWAYS);

                correctAnswerRadios[i] = new RadioButton("Correct");
                correctAnswerRadios[i].setToggleGroup(correctAnswerGroup);

                choiceBox.getChildren().addAll(choiceNumLabel, choiceFields[i], correctAnswerRadios[i]);
                choicesBox.getChildren().add(choiceBox);
            }

            correctAnswerRadios[0].setSelected(true);

            container.getChildren().addAll(
                headerLabel,
                new Separator(),
                questionLabel,
                questionTextArea,
                choicesLabel,
                choicesBox
            );
        }

        public VBox getContainer() {
            return container;
        }

        public int getQuestionNumber() {
            return questionNumber;
        }

        public Question getQuestion() throws IllegalArgumentException {
            String questionText = questionTextArea.getText().trim();
            if (questionText.isEmpty()) {
                throw new IllegalArgumentException("Question text cannot be empty!");
            }

            List<String> choices = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                String choice = choiceFields[i].getText().trim();
                if (choice.isEmpty()) {
                    throw new IllegalArgumentException("Choice " + (i + 1) + " cannot be empty!");
                }
                choices.add(choice);
            }

            int correctIndex = -1;
            for (int i = 0; i < 4; i++) {
                if (correctAnswerRadios[i].isSelected()) {
                    correctIndex = i;
                    break;
                }
            }

            if (correctIndex == -1) {
                throw new IllegalArgumentException("Please select a correct answer!");
            }

            return new Question(0, questionText, choices, correctIndex);
        }
    }
}

