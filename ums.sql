CREATE DATABASE UMS_DB;
USE UMS_DB;

-- Main Users table
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       role ENUM('ADMIN', 'STUDENT', 'INSTRUCTOR') NOT NULL
);

-- Students table (extends users)
CREATE TABLE students (
                          user_id INT PRIMARY KEY,
                          level INT,
                          major VARCHAR(100),
                          grade DOUBLE,
                          department ENUM('CS', 'IS', 'IT', 'AI') NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Instructors table (extends users)
CREATE TABLE instructors (
                             user_id INT PRIMARY KEY,
                             department ENUM('CS', 'IS', 'IT', 'AI') NOT NULL,
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Admins table (extends users)
CREATE TABLE admins (
                        user_id INT PRIMARY KEY,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Courses Table
CREATE TABLE courses (
                         code VARCHAR(10) PRIMARY KEY,
                         course_name VARCHAR(100) NOT NULL,
                         level VARCHAR(20),
                         major VARCHAR(100),
                         lecture_time VARCHAR(50),
                         instructor_id INT,
                         FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE SET NULL
);


-- QUIZZES Table

CREATE TABLE quizzes (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         course_code VARCHAR(10) NOT NULL,
                         FOREIGN KEY (course_code) REFERENCES courses(code) ON DELETE CASCADE
);


-- QUESTIONS Table

CREATE TABLE questions (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           quiz_id INT NOT NULL,
                           text TEXT NOT NULL,
                           option1 VARCHAR(255),
                           option2 VARCHAR(255),
                           option3 VARCHAR(255),
                           option4 VARCHAR(255),
                           correct_option_index INT,
                           FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- QUIZ RESULTS Table

CREATE TABLE quiz_results (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              student_id INT NOT NULL,
                              quiz_id INT NOT NULL,
                              score INT NOT NULL,
                              FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
                              FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);


-- QUIZ ANSWERS Table

CREATE TABLE quiz_answers (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              result_id INT NOT NULL,
                              question_id INT NOT NULL,
                              chosen_answer VARCHAR(255),
                              FOREIGN KEY (result_id) REFERENCES quiz_results(id) ON DELETE CASCADE,
                              FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE student_courses (
                                 student_id INT,
                                 course_code VARCHAR(10),
                                 PRIMARY KEY (student_id, course_code),
                                 FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
                                 FOREIGN KEY (course_code) REFERENCES courses(code) ON DELETE CASCADE
);

