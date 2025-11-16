-- ============================================
-- Mock Data for University Management System
-- ============================================
-- Generated: November 16, 2025
-- Note: All passwords are hashed using BCrypt
-- Default password for all users: "Pass123!"
-- ============================================

USE UMS_DB;

-- Disable foreign key checks temporarily for clean insertion
SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data
TRUNCATE TABLE quiz_answers;
TRUNCATE TABLE quiz_results;
TRUNCATE TABLE questions;
TRUNCATE TABLE quizzes;
TRUNCATE TABLE student_courses;
TRUNCATE TABLE courses;
TRUNCATE TABLE students;
TRUNCATE TABLE instructors;
TRUNCATE TABLE admins;
TRUNCATE TABLE users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- USERS DATA
-- ============================================
-- Password: Pass123! (BCrypt hashed: $2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG)

-- Admins (3 users)
INSERT INTO users (id, name, email, password, role) VALUES
(1, 'John Smith', 'john.smith@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'ADMIN'),
(2, 'Sarah Johnson', 'sarah.johnson@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'ADMIN'),
(3, 'Michael Brown', 'michael.brown@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'ADMIN');

-- Instructors (8 users)
INSERT INTO users (id, name, email, password, role) VALUES
(4, 'Dr. Emily Davis', 'emily.davis@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(5, 'Dr. Robert Wilson', 'robert.wilson@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(6, 'Dr. Jennifer Martinez', 'jennifer.martinez@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(7, 'Dr. David Anderson', 'david.anderson@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(8, 'Dr. Lisa Taylor', 'lisa.taylor@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(9, 'Dr. James Thomas', 'james.thomas@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(10, 'Dr. Maria Garcia', 'maria.garcia@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR'),
(11, 'Dr. Christopher Lee', 'christopher.lee@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'INSTRUCTOR');

-- Students (20 users)
INSERT INTO users (id, name, email, password, role) VALUES
(12, 'Alice Cooper', 'alice.cooper@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(13, 'Bob Miller', 'bob.miller@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(14, 'Carol White', 'carol.white@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(15, 'Daniel Harris', 'daniel.harris@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(16, 'Emma Clark', 'emma.clark@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(17, 'Frank Lewis', 'frank.lewis@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(18, 'Grace Walker', 'grace.walker@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(19, 'Henry Hall', 'henry.hall@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(20, 'Isabella Allen', 'isabella.allen@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(21, 'Jack Young', 'jack.young@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(22, 'Kate King', 'kate.king@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(23, 'Liam Scott', 'liam.scott@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(24, 'Mia Green', 'mia.green@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(25, 'Noah Adams', 'noah.adams@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(26, 'Olivia Baker', 'olivia.baker@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(27, 'Peter Nelson', 'peter.nelson@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(28, 'Quinn Carter', 'quinn.carter@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(29, 'Rachel Mitchell', 'rachel.mitchell@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(30, 'Samuel Perez', 'samuel.perez@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT'),
(31, 'Tina Roberts', 'tina.roberts@gmail.com', '$2b$12$5NymyMVAn/MhxM93jZ/bj.uQaWRhw5Q4YxEjiR/WSZy4vcoLMA6oG', 'STUDENT');

-- ============================================
-- ADMINS DATA
-- ============================================
INSERT INTO admins (user_id) VALUES
(1),
(2),
(3);

-- ============================================
-- INSTRUCTORS DATA
-- ============================================
INSERT INTO instructors (user_id, department) VALUES
(4, 'CS'),   -- Dr. Emily Davis
(5, 'CS'),   -- Dr. Robert Wilson
(6, 'IS'),   -- Dr. Jennifer Martinez
(7, 'IS'),   -- Dr. David Anderson
(8, 'IT'),   -- Dr. Lisa Taylor
(9, 'IT'),   -- Dr. James Thomas
(10, 'AI'),  -- Dr. Maria Garcia
(11, 'AI');  -- Dr. Christopher Lee

-- ============================================
-- STUDENTS DATA
-- ============================================
INSERT INTO students (user_id, level, major, grade, department) VALUES
-- Level 1 Students (CS)
(12, 1, 'Software Engineering', 0.0, 'CS'),
(13, 1, 'Software Engineering', 0.0, 'CS'),
(14, 1, 'Computer Science', 0.0, 'CS'),
-- Level 1 Students (IS)
(15, 1, 'Information Systems', 0.0, 'IS'),
(16, 1, 'Information Systems', 0.0, 'IS'),
-- Level 2 Students (CS)
(17, 2, 'Software Engineering', 0.0, 'CS'),
(18, 2, 'Computer Science', 0.0, 'CS'),
-- Level 2 Students (IT)
(19, 2, 'Network Engineering', 0.0, 'IT'),
(20, 2, 'Cybersecurity', 0.0, 'IT'),
-- Level 3 Students (AI)
(21, 3, 'Artificial Intelligence', 0.0, 'AI'),
(22, 3, 'Machine Learning', 0.0, 'AI'),
(23, 3, 'Artificial Intelligence', 0.0, 'AI'),
-- Level 3 Students (CS)
(24, 3, 'Software Engineering', 0.0, 'CS'),
-- Level 4 Students (CS)
(25, 4, 'Software Engineering', 0.0, 'CS'),
(26, 4, 'Computer Science', 0.0, 'CS'),
-- Level 4 Students (IS)
(27, 4, 'Information Systems', 0.0, 'IS'),
-- Level 4 Students (IT)
(28, 4, 'Network Engineering', 0.0, 'IT'),
(29, 4, 'Cybersecurity', 0.0, 'IT'),
-- Level 4 Students (AI)
(30, 4, 'Artificial Intelligence', 0.0, 'AI'),
(31, 4, 'Machine Learning', 0.0, 'AI');

-- ============================================
-- COURSES DATA
-- ============================================

-- Level 1 Courses
INSERT INTO courses (code, course_name, level, major, lecture_time, instructor_id) VALUES
-- CS Level 1
('CS101', 'Introduction to Programming', '1', 'Software Engineering', 'Mon 9:00-11:00', 4),
('CS102', 'Data Structures Fundamentals', '1', 'Software Engineering', 'Tue 10:00-12:00', 5),
('CS103', 'Computer Organization', '1', 'Computer Science', 'Wed 14:00-16:00', 4),
-- IS Level 1
('IS101', 'Information Systems Basics', '1', 'Information Systems', 'Thu 9:00-11:00', 6),
('IS102', 'Database Fundamentals', '1', 'Information Systems', 'Fri 10:00-12:00', 7),

-- Level 2 Courses
-- CS Level 2
('CS201', 'Object-Oriented Programming', '2', 'Software Engineering', 'Mon 13:00-15:00', 5),
('CS202', 'Algorithms and Complexity', '2', 'Computer Science', 'Tue 14:00-16:00', 4),
-- IT Level 2
('IT201', 'Network Fundamentals', '2', 'Network Engineering', 'Wed 10:00-12:00', 8),
('IT202', 'Cybersecurity Basics', '2', 'Cybersecurity', 'Thu 13:00-15:00', 9),

-- Level 3 Courses
-- AI Level 3
('AI301', 'Machine Learning Fundamentals', '3', 'Artificial Intelligence', 'Mon 10:00-12:00', 10),
('AI302', 'Neural Networks', '3', 'Machine Learning', 'Tue 9:00-11:00', 11),
('AI303', 'Deep Learning', '3', 'Artificial Intelligence', 'Wed 13:00-15:00', 10),
-- CS Level 3
('CS301', 'Software Engineering Principles', '3', 'Software Engineering', 'Thu 10:00-12:00', 5),

-- Level 4 Courses
-- CS Level 4
('CS401', 'Advanced Software Architecture', '4', 'Software Engineering', 'Mon 14:00-16:00', 5),
('CS402', 'Compiler Design', '4', 'Computer Science', 'Tue 13:00-15:00', 4),
-- IS Level 4
('IS401', 'Enterprise Systems', '4', 'Information Systems', 'Wed 9:00-11:00', 6),
-- IT Level 4
('IT401', 'Advanced Network Security', '4', 'Network Engineering', 'Thu 14:00-16:00', 8),
('IT402', 'Ethical Hacking', '4', 'Cybersecurity', 'Fri 10:00-12:00', 9),
-- AI Level 4
('AI401', 'Advanced AI Applications', '4', 'Artificial Intelligence', 'Mon 11:00-13:00', 10),
('AI402', 'Natural Language Processing', '4', 'Machine Learning', 'Wed 10:00-12:00', 11);

-- ============================================
-- STUDENT COURSE ENROLLMENTS
-- ============================================

-- Level 1 Students
INSERT INTO student_courses (student_id, course_code) VALUES
-- Alice Cooper (CS, Level 1)
(12, 'CS101'),
(12, 'CS102'),
-- Bob Miller (CS, Level 1)
(13, 'CS101'),
(13, 'CS102'),
-- Carol White (CS, Level 1)
(14, 'CS103'),
(14, 'CS101'),
-- Daniel Harris (IS, Level 1)
(15, 'IS101'),
(15, 'IS102'),
-- Emma Clark (IS, Level 1)
(16, 'IS101'),
(16, 'IS102');

-- Level 2 Students
INSERT INTO student_courses (student_id, course_code) VALUES
-- Frank Lewis (CS, Level 2)
(17, 'CS201'),
(17, 'CS202'),
-- Grace Walker (CS, Level 2)
(18, 'CS201'),
(18, 'CS202'),
-- Henry Hall (IT, Level 2)
(19, 'IT201'),
(19, 'IT202'),
-- Isabella Allen (IT, Level 2)
(20, 'IT201'),
(20, 'IT202');

-- Level 3 Students
INSERT INTO student_courses (student_id, course_code) VALUES
-- Jack Young (AI, Level 3)
(21, 'AI301'),
(21, 'AI302'),
-- Kate King (AI, Level 3)
(22, 'AI301'),
(22, 'AI302'),
-- Liam Scott (AI, Level 3)
(23, 'AI301'),
(23, 'AI303'),
-- Mia Green (CS, Level 3)
(24, 'CS301');

-- Level 4 Students
INSERT INTO student_courses (student_id, course_code) VALUES
-- Noah Adams (CS, Level 4)
(25, 'CS401'),
(25, 'CS402'),
-- Olivia Baker (CS, Level 4)
(26, 'CS401'),
(26, 'CS402'),
-- Peter Nelson (IS, Level 4)
(27, 'IS401'),
-- Quinn Carter (IT, Level 4)
(28, 'IT401'),
(28, 'IT402'),
-- Rachel Mitchell (IT, Level 4)
(29, 'IT401'),
(29, 'IT402'),
-- Samuel Perez (AI, Level 4)
(30, 'AI401'),
(30, 'AI402'),
-- Tina Roberts (AI, Level 4)
(31, 'AI401'),
(31, 'AI402');

-- ============================================
-- QUIZZES DATA
-- ============================================

-- CS101 Quizzes
INSERT INTO quizzes (id, title, course_code) VALUES
(1, 'Programming Basics Quiz', 'CS101'),
(2, 'Variables and Data Types', 'CS101'),
-- CS102 Quizzes
(3, 'Arrays and Lists Quiz', 'CS102'),
(4, 'Stack and Queue Concepts', 'CS102'),
-- CS201 Quizzes
(5, 'OOP Fundamentals', 'CS201'),
(6, 'Inheritance and Polymorphism', 'CS201'),
-- AI301 Quizzes
(7, 'ML Algorithms Quiz', 'AI301'),
(8, 'Supervised Learning', 'AI301'),
-- IT201 Quizzes
(9, 'OSI Model Quiz', 'IT201'),
(10, 'TCP/IP Fundamentals', 'IT201');

-- ============================================
-- QUESTIONS DATA
-- ============================================

-- Quiz 1: Programming Basics Quiz (CS101)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(1, 'What is a variable in programming?', 'A constant value', 'A container for storing data', 'A function', 'A loop', 1),
(1, 'Which of the following is NOT a primitive data type?', 'int', 'String', 'boolean', 'float', 1),
(1, 'What does IDE stand for?', 'Internal Development Environment', 'Integrated Development Environment', 'Internet Development Engine', 'Intelligent Design Editor', 1),
(1, 'What is the purpose of a compiler?', 'To run programs', 'To translate code to machine language', 'To debug code', 'To write code', 1),
(1, 'Which symbol is used for single-line comments in Java?', '#', '//', '<!--', '/*', 1);

-- Quiz 2: Variables and Data Types (CS101)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(2, 'Which data type would you use for storing a decimal number?', 'int', 'boolean', 'double', 'char', 2),
(2, 'What is the default value of a boolean variable?', 'true', 'false', 'null', '0', 1),
(2, 'Which operator is used for string concatenation in Java?', '-', '+', '*', '/', 1),
(2, 'What is type casting?', 'Converting one data type to another', 'Creating new types', 'Deleting types', 'Comparing types', 0),
(2, 'Which keyword is used to declare a constant in Java?', 'const', 'final', 'static', 'fixed', 1);

-- Quiz 3: Arrays and Lists Quiz (CS102)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(3, 'What is the index of the first element in an array?', '-1', '0', '1', 'Depends on language', 1),
(3, 'Which data structure uses LIFO principle?', 'Queue', 'Array', 'Stack', 'List', 2),
(3, 'What is the time complexity of accessing an element in an array by index?', 'O(n)', 'O(log n)', 'O(1)', 'O(n^2)', 2),
(3, 'Which of the following is a dynamic data structure?', 'Array', 'ArrayList', 'Static Array', 'Fixed Array', 1),
(3, 'What does ArrayList resize by when it runs out of space?', '10%', '50%', '100%', '200%', 1);

-- Quiz 4: Stack and Queue Concepts (CS102)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(4, 'Which operation adds an element to a stack?', 'enqueue', 'push', 'insert', 'add', 1),
(4, 'What is the FIFO principle?', 'First In First Out', 'Fast In Fast Out', 'Front In Front Out', 'Final In Final Out', 0),
(4, 'Which data structure is used in BFS traversal?', 'Stack', 'Queue', 'Array', 'Tree', 1),
(4, 'What is the top of the stack?', 'First element', 'Last element', 'Middle element', 'Random element', 1),
(4, 'Which operation removes an element from a queue?', 'pop', 'dequeue', 'remove', 'delete', 1);

-- Quiz 5: OOP Fundamentals (CS201)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(5, 'What does OOP stand for?', 'Object Oriented Programming', 'Optimal Ordered Process', 'Object Order Protocol', 'Organized Object Program', 0),
(5, 'Which principle allows hiding implementation details?', 'Inheritance', 'Polymorphism', 'Encapsulation', 'Abstraction', 2),
(5, 'What is a class in OOP?', 'A function', 'A blueprint for objects', 'A variable', 'A loop', 1),
(5, 'Which keyword is used to create an object in Java?', 'create', 'new', 'make', 'object', 1),
(5, 'What is a constructor?', 'A destructor', 'A special method to initialize objects', 'A loop', 'A variable', 1);

-- Quiz 6: Inheritance and Polymorphism (CS201)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(6, 'What is inheritance in OOP?', 'Copying code', 'Acquiring properties from parent class', 'Creating new classes', 'Deleting classes', 1),
(6, 'Which keyword is used for inheritance in Java?', 'inherits', 'extends', 'implements', 'derives', 1),
(6, 'What is method overriding?', 'Creating new methods', 'Redefining parent method in child class', 'Deleting methods', 'Hiding methods', 1),
(6, 'What is polymorphism?', 'Many forms', 'Single form', 'No form', 'Fixed form', 0),
(6, 'Can a class inherit from multiple classes in Java?', 'Yes', 'No', 'Sometimes', 'Depends on version', 1);

-- Quiz 7: ML Algorithms Quiz (AI301)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(7, 'What is machine learning?', 'Manual programming', 'Computers learning from data', 'Hardware learning', 'Network learning', 1),
(7, 'Which is a supervised learning algorithm?', 'K-means', 'Linear Regression', 'PCA', 'Apriori', 1),
(7, 'What is a training set?', 'Test data', 'Data used to train model', 'Validation data', 'Production data', 1),
(7, 'What is overfitting?', 'Model fits training data too well', 'Model does not fit', 'Model is perfect', 'Model is simple', 0),
(7, 'Which metric evaluates classification models?', 'MSE', 'RMSE', 'Accuracy', 'R-squared', 2);

-- Quiz 8: Supervised Learning (AI301)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(8, 'What is supervised learning?', 'Learning without labels', 'Learning with labeled data', 'Reinforcement learning', 'Unsupervised learning', 1),
(8, 'Which algorithm is used for classification?', 'K-means', 'Decision Tree', 'PCA', 'Apriori', 1),
(8, 'What is a label in supervised learning?', 'Input feature', 'Output variable', 'Model parameter', 'Loss function', 1),
(8, 'What is gradient descent?', 'Ascending algorithm', 'Optimization algorithm', 'Classification algorithm', 'Clustering algorithm', 1),
(8, 'What is cross-validation?', 'Training technique', 'Model evaluation technique', 'Data cleaning', 'Feature engineering', 1);

-- Quiz 9: OSI Model Quiz (IT201)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(9, 'How many layers are in the OSI model?', '5', '7', '9', '11', 1),
(9, 'Which layer is responsible for routing?', 'Data Link', 'Network', 'Transport', 'Application', 1),
(9, 'What does TCP stand for?', 'Transfer Control Protocol', 'Transmission Control Protocol', 'Transport Control Protocol', 'Technical Control Protocol', 1),
(9, 'Which layer handles physical transmission?', 'Physical Layer', 'Data Link Layer', 'Network Layer', 'Transport Layer', 0),
(9, 'What is the main function of the Transport layer?', 'Routing', 'End-to-end communication', 'Physical transmission', 'Data encryption', 1);

-- Quiz 10: TCP/IP Fundamentals (IT201)
INSERT INTO questions (quiz_id, text, option1, option2, option3, option4, correct_option_index) VALUES
(10, 'What is an IP address?', 'Physical address', 'Logical network address', 'MAC address', 'Port number', 1),
(10, 'Which protocol is connection-oriented?', 'UDP', 'TCP', 'ICMP', 'IP', 1),
(10, 'What is the purpose of DNS?', 'Encrypt data', 'Translate domain names to IP', 'Route packets', 'Compress data', 1),
(10, 'What is a subnet mask?', 'Network identifier', 'Host identifier', 'Both network and host identifier', 'MAC address', 0),
(10, 'Which port does HTTP use by default?', '21', '22', '80', '443', 2);

-- ============================================
-- QUIZ RESULTS DATA (Sample)
-- ============================================

-- Student results for Quiz 1 (Programming Basics)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(1, 12, 1, 4),  -- Alice: 4/5
(2, 13, 1, 5),  -- Bob: 5/5
(3, 14, 1, 3);  -- Carol: 3/5

-- Student results for Quiz 2 (Variables and Data Types)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(4, 12, 2, 4),  -- Alice: 4/5
(5, 13, 2, 4);  -- Bob: 4/5

-- Student results for Quiz 3 (Arrays and Lists)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(6, 12, 3, 5),  -- Alice: 5/5
(7, 13, 3, 4);  -- Bob: 4/5

-- Student results for Quiz 5 (OOP Fundamentals)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(8, 17, 5, 5),  -- Frank: 5/5
(9, 18, 5, 4);  -- Grace: 4/5

-- Student results for Quiz 7 (ML Algorithms)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(10, 21, 7, 4),  -- Jack: 4/5
(11, 22, 7, 5),  -- Kate: 5/5
(12, 23, 7, 3);  -- Liam: 3/5

-- Student results for Quiz 9 (OSI Model)
INSERT INTO quiz_results (id, student_id, quiz_id, score) VALUES
(13, 19, 9, 4),  -- Henry: 4/5
(14, 20, 9, 5);  -- Isabella: 5/5

-- ============================================
-- UPDATE STUDENT GRADES BASED ON QUIZ RESULTS
-- ============================================

-- Calculate and update grades for students who took quizzes
-- Grade calculation: (total score / total possible points) * 100

-- Alice Cooper: (4+4+5)/(5+5+5) = 13/15 = 86.67%
UPDATE students SET grade = 86.67 WHERE user_id = 12;

-- Bob Miller: (5+4+4)/(5+5+5) = 13/15 = 86.67%
UPDATE students SET grade = 86.67 WHERE user_id = 13;

-- Carol White: 3/5 = 60%
UPDATE students SET grade = 60.00 WHERE user_id = 14;

-- Frank Lewis: 5/5 = 100%
UPDATE students SET grade = 100.00 WHERE user_id = 17;

-- Grace Walker: 4/5 = 80%
UPDATE students SET grade = 80.00 WHERE user_id = 18;

-- Henry Hall: 4/5 = 80%
UPDATE students SET grade = 80.00 WHERE user_id = 19;

-- Isabella Allen: 5/5 = 100%
UPDATE students SET grade = 100.00 WHERE user_id = 20;

-- Jack Young: 4/5 = 80%
UPDATE students SET grade = 80.00 WHERE user_id = 21;

-- Kate King: 5/5 = 100%
UPDATE students SET grade = 100.00 WHERE user_id = 22;

-- Liam Scott: 3/5 = 60%
UPDATE students SET grade = 60.00 WHERE user_id = 23;

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- View all users by role
SELECT 'ADMINS' as category, COUNT(*) as count FROM admins
UNION ALL
SELECT 'INSTRUCTORS', COUNT(*) FROM instructors
UNION ALL
SELECT 'STUDENTS', COUNT(*) FROM students;

-- View all courses by level
SELECT level, COUNT(*) as course_count
FROM courses
GROUP BY level
ORDER BY level;

-- View students with grades
SELECT u.name, s.level, s.major, s.grade
FROM users u
JOIN students s ON u.id = s.user_id
WHERE s.grade > 0
ORDER BY s.grade DESC;

-- View quiz completion statistics
SELECT
    u.name as student_name,
    COUNT(qr.id) as quizzes_taken,
    SUM(qr.score) as total_score,
    s.grade as overall_grade
FROM users u
JOIN students s ON u.id = s.user_id
LEFT JOIN quiz_results qr ON s.user_id = qr.student_id
GROUP BY u.id, u.name, s.grade
HAVING COUNT(qr.id) > 0
ORDER BY s.grade DESC;

-- ============================================
-- COMPLETION MESSAGE
-- ============================================
SELECT '===============================================' as '';
SELECT 'Mock Data Successfully Inserted!' as '';
SELECT '===============================================' as '';
SELECT 'Summary:' as '';
SELECT '  - 3 Admins (Default password: Pass123!)' as '';
SELECT '  - 8 Instructors (Default password: Pass123!)' as '';
SELECT '  - 20 Students (Default password: Pass123!)' as '';
SELECT '  - 21 Courses across 4 levels' as '';
SELECT '  - 10 Quizzes with 50 Questions' as '';
SELECT '  - 14 Quiz Results' as '';
SELECT '===============================================' as '';
SELECT 'You can now login with:' as '';
SELECT '  Admin: john.smith@gmail.com / Pass123!' as '';
SELECT '  Instructor: emily.davis@gmail.com / Pass123!' as '';
SELECT '  Student: alice.cooper@gmail.com / Pass123!' as '';
SELECT '===============================================' as '';

