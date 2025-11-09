<div align="center">

# 🎓 University Management System (UMS)

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)](https://mariadb.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

**A comprehensive console-based University Management System built with Java, implementing role-based access control for Admins, Instructors, and Students.**

[Features](#-features) • [Architecture](#-architecture) • [Installation](#-installation) • [Usage](#-usage) • [Database](#-database-schema) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [About](#-about-the-project)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technologies](#-technologies-used)
- [Installation](#-installation)
- [Usage](#-usage)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📖 About The Project

The **University Management System (UMS)** is a robust, console-based Java application designed to streamline university operations. It provides a complete solution for managing courses, students, instructors, quizzes, and enrollments with a secure role-based authentication system.

### 🎯 Key Highlights

- **Role-Based Access Control**: Three distinct user roles (Admin, Instructor, Student) with specific permissions
- **Complete CRUD Operations**: Full Create, Read, Update, and Delete functionality for all entities
- **Quiz Management System**: Comprehensive quiz creation, administration, and result tracking
- **Course Enrollment**: Seamless student course registration and management
- **Secure Authentication**: Email-based login system with password protection
- **Database-Driven**: Persistent data storage using MariaDB
- **Modular Architecture**: Clean separation of concerns with DAO, Service, and Model layers

---

## ✨ Features

### 👨‍💼 Admin Features
- ✅ **User Management**
  - Create new users (Admin, Instructor, Student)
  - Delete existing users
  - View all users in the system
- ✅ **Course Management**
  - Create new courses
  - Delete courses
  - View all courses
  - Assign instructors to courses
- ✅ **System Administration**
  - Full access to all system resources
  - User role assignment

### 👨‍🏫 Instructor Features
- ✅ **Quiz Management**
  - Create quizzes for assigned courses
  - Add multiple-choice questions
  - Set correct answers
- ✅ **Course Access**
  - View assigned courses
  - View all quizzes created
- ✅ **Student Monitoring**
  - Track quiz results
  - View student performance

### 👨‍🎓 Student Features
- ✅ **Course Registration**
  - Register for available courses
  - View enrolled courses
  - View course details (instructor, schedule)
- ✅ **Quiz Participation**
  - Take quizzes for enrolled courses
  - View quiz results immediately
  - Track quiz history
- ✅ **Academic Progress**
  - View grades and scores
  - Monitor course enrollment status

---

## 🏗️ Architecture

This project follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────┐
│     Presentation Layer (Main)       │  ← User Interface (Console)
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│        Service Layer                │  ← Business Logic
│  (AdminService, CourseService, etc) │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│         DAO Layer                   │  ← Data Access
│  (AdminDAO, CourseDAO, etc)         │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│         Model Layer                 │  ← Domain Entities
│  (User, Course, Quiz, etc)          │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│       Database (MariaDB)            │  ← Data Persistence
└─────────────────────────────────────┘
```

### Design Patterns Used

- **DAO (Data Access Object)**: Abstracts database operations
- **Singleton Pattern**: DatabaseConnection class ensures single DB connection
- **Service Layer Pattern**: Encapsulates business logic
- **Inheritance**: User class extended by Admin, Instructor, and Student
- **Enum Pattern**: Role and Department enumerations

---

## 🛠️ Technologies Used

| Technology | Purpose | Version |
|------------|---------|---------|
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) | Core Programming Language | 25 |
| ![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven&logoColor=white) | Build & Dependency Management | 4.0.0 |
| ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat&logo=mariadb&logoColor=white) | Database Management System | Latest |
| ![JDBC](https://img.shields.io/badge/JDBC-007396?style=flat&logo=java&logoColor=white) | Database Connectivity | MariaDB Connector 3.3.0 |
| ![Dotenv](https://img.shields.io/badge/Dotenv-3.0.0-green?style=flat) | Environment Configuration | 3.0.0 |

---

## 🚀 Installation

### Prerequisites

Before you begin, ensure you have the following installed:

- ☑️ **Java JDK 25** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- ☑️ **Apache Maven** ([Download](https://maven.apache.org/download.cgi))
- ☑️ **MariaDB** or **MySQL** ([Download](https://mariadb.org/download/))
- ☑️ **Git** ([Download](https://git-scm.com/downloads))

### Step-by-Step Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/AIM-UMS.git
   cd AIM-UMS
   ```

2. **Set Up the Database**
   ```bash
   # Login to MariaDB
   mysql -u root -p
   
   # Execute the SQL script
   source ums.sql
   ```

3. **Configure Database Connection**
   
   Create a `.env` file in the project root:
   ```env
   DB_URL=jdbc:mariadb://localhost:3306/UMS_DB
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn exec:java -Dexec.mainClass="com.ums.system.Main"
   ```
   
   Or run the compiled JAR:
   ```bash
   java -jar target/UMS-1.0-SNAPSHOT.jar
   ```

---

## 💻 Usage

### Login System

When you start the application, you'll be greeted with a login screen:

```
===================================
Welcome to University Management System
===================================

--- Login ---
Email: admin@ums.com
Password: ********
```

### Default Users

The system comes with pre-configured users for testing:

| Role | Email | Password | Access Level |
|------|-------|----------|--------------|
| Admin | admin@ums.com | admin123 | Full System Access |
| Instructor | instructor@ums.com | inst123 | Course & Quiz Management |
| Student | student@ums.com | stud123 | Course Registration & Quizzes |

### Admin Workflow Example

```
========== ADMIN MENU ==========
1. Create Course
2. Delete Course
3. Create User
4. Delete User
5. View All Courses
6. View All Users
7. Logout

Choose an option: 1
Enter course code: CS101
Enter course name: Introduction to Programming
Enter level: 1
Enter major: Computer Science
Enter lecture time: Mon/Wed 10:00-11:30
Enter instructor ID: 2
Course created successfully!
```

### Instructor Workflow Example

```
========== INSTRUCTOR MENU ==========
1. Create Quiz
2. View Assigned Courses
3. View All Quizzes
4. Logout

Choose an option: 1
Enter quiz title: Java Basics Quiz
Enter course code: CS101
How many questions? 5
[Questions entry process...]
Quiz created successfully!
```

### Student Workflow Example

```
========== STUDENT MENU ==========
1. Register for Course
2. View My Courses
3. View Course Details
4. Take Quiz
5. Logout

Choose an option: 1
Enter course code to register: CS101
Successfully registered for CS101!
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
                              ┌──────────────────┐
                              │      USERS       │
                              ├──────────────────┤
                              │ id (PK)          │
                              │ name             │
                              │ email (UNIQUE)   │
                              │ password         │
                              │ role (ENUM)      │
                              └────────┬─────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    │                  │                  │
                    ▼                  ▼                  ▼
         ┌──────────────────┐ ┌──────────────┐ ┌──────────────┐
         │    STUDENTS      │ │ INSTRUCTORS  │ │    ADMINS    │
         ├──────────────────┤ ├──────────────┤ ├──────────────┤
         │ user_id (PK,FK)  │ │user_id(PK,FK)│ │user_id(PK,FK)│
         │ level            │ │ department   │ └──────────────┘
         │ major            │ └──────┬───────┘
         │ grade            │        │
         │ department       │        │ instructor_id (FK)
         └────────┬─────────┘        │
                  │                  ▼
                  │         ┌──────────────────┐
                  │         │     COURSES      │
                  │         ├──────────────────┤
                  │         │ code (PK)        │
                  │         │ course_name      │
                  │         │ level            │
                  │         │ major            │
                  │         │ lecture_time     │
                  │         │ instructor_id(FK)│
                  │         └────────┬─────────┘
                  │                  │
                  │         ┌────────┼────────┐
                  │         │                 │
                  │         │ course_code(FK) │
                  │         │                 │
                  │         ▼                 ▼
         ┌────────▼──────────────┐   ┌──────────────────┐
         │  STUDENT_COURSES      │   │     QUIZZES      │
         │  (Junction Table)     │   ├──────────────────┤
         ├───────────────────────┤   │ id (PK)          │
         │ student_id (PK,FK)    │   │ title            │
         │ course_code (PK,FK)   │   │ course_code (FK) │
         └───────────────────────┘   └────────┬─────────┘
                                              │
                                              │ quiz_id (FK)
                                     ┌────────┼────────┐
                                     │                 │
                                     ▼                 ▼
                            ┌──────────────┐  ┌──────────────────┐
                            │  QUESTIONS   │  │  QUIZ_RESULTS    │
                            ├──────────────┤  ├──────────────────┤
                            │ id (PK)      │  │ id (PK)          │
                            │ quiz_id (FK) │  │ student_id (FK)  │
                            │ text         │  │ quiz_id (FK)     │
                            │ option1      │  │ score            │
                            │ option2      │  └────────┬─────────┘
                            │ option3      │           │
                            │ option4      │           │ result_id (FK)
                            │ correct_opt  │           │
                            └──────┬───────┘           │
                                   │                   │
                                   │ question_id (FK)  │
                                   │                   │
                                   └────────┬──────────┘
                                            │
                                            ▼
                                   ┌──────────────────┐
                                   │  QUIZ_ANSWERS    │
                                   ├──────────────────┤
                                   │ id (PK)          │
                                   │ result_id (FK)   │
                                   │ question_id (FK) │
                                   │ chosen_answer    │
                                   └──────────────────┘
```

### Key Tables

#### 1. **users** - Core user information
```sql
- id (Primary Key)
- name, email, password
- role (ADMIN, STUDENT, INSTRUCTOR)
```

#### 2. **students** - Student-specific data
```sql
- user_id (Foreign Key → users)
- level, major, grade
- department (CS, IS, IT, AI)
```

#### 3. **instructors** - Instructor-specific data
```sql
- user_id (Foreign Key → users)
- department (CS, IS, IT, AI)
```

#### 4. **courses** - Course information
```sql
- code (Primary Key)
- course_name, level, major
- lecture_time
- instructor_id (Foreign Key → users)
```

#### 5. **quizzes** - Quiz information
```sql
- id (Primary Key)
- title
- course_code (Foreign Key → courses)
```

#### 6. **questions** - Quiz questions
```sql
- id (Primary Key)
- quiz_id (Foreign Key → quizzes)
- text, option1-4
- correct_option_index
```

#### 7. **quiz_results** - Student quiz scores
```sql
- id (Primary Key)
- student_id, quiz_id (Foreign Keys)
- score
```

#### 8. **student_courses** - Junction table for student enrollments
```sql
- student_id (Foreign Key → students)
- course_code (Foreign Key → courses)
```

#### 9. **quiz_answers** - Student answers to quiz questions
```sql
- id (Primary Key)
- result_id (Foreign Key → quiz_results)
- question_id (Foreign Key → questions)
- chosen_answer
```

---

### Database Relationships Explained

#### **Core Relationships:**

1. **User Inheritance Hierarchy**
   - `users` table is the parent table
   - `students`, `instructors`, and `admins` extend users via `user_id` foreign key
   - One-to-One relationship (inheritance pattern)
   - Cascade delete: Deleting a user removes their role-specific data

2. **Course-Instructor Relationship**
   - `courses.instructor_id` → `users.id` (One-to-Many)
   - One instructor can teach multiple courses
   - SET NULL on delete: Course remains if instructor is deleted

3. **Student-Course Enrollment (Many-to-Many)**
   - `student_courses` junction table connects students and courses
   - `student_courses.student_id` → `students.user_id`
   - `student_courses.course_code` → `courses.code`
   - Allows students to register for multiple courses
   - Cascade delete: Enrollment removed if student or course is deleted

4. **Course-Quiz Relationship (One-to-Many)**
   - `quizzes.course_code` → `courses.code`
   - One course can have multiple quizzes
   - Cascade delete: Quizzes deleted when course is removed

5. **Quiz-Question Relationship (One-to-Many)**
   - `questions.quiz_id` → `quizzes.id`
   - One quiz contains multiple questions
   - Cascade delete: Questions deleted when quiz is removed

6. **Quiz Results (Many-to-Many with attributes)**
   - `quiz_results` links students and quizzes with score
   - `quiz_results.student_id` → `students.user_id`
   - `quiz_results.quiz_id` → `quizzes.id`
   - Tracks which student took which quiz and their score

7. **Quiz Answers Tracking**
   - `quiz_answers` stores individual question responses
   - `quiz_answers.result_id` → `quiz_results.id`
   - `quiz_answers.question_id` → `questions.id`
   - Records each answer a student gave in a quiz attempt

---

## 📁 Project Structure

```
AIM-UMS/
│
├── src/main/java/com/ums/system/
│   ├── Main.java                      # Application entry point
│   │
│   ├── model/                         # Domain models
│   │   ├── User.java                  # Base user class
│   │   ├── Admin.java                 # Admin entity
│   │   ├── Instructor.java            # Instructor entity
│   │   ├── Student.java               # Student entity
│   │   ├── Course.java                # Course entity
│   │   ├── Quiz.java                  # Quiz entity
│   │   ├── Question.java              # Question entity
│   │   ├── QuizResult.java            # Quiz result entity
│   │   ├── Role.java                  # Role enumeration
│   │   └── Department.java            # Department enumeration
│   │
│   ├── dao/                           # Data Access Objects
│   │   ├── UserDAO.java               # User DAO interface
│   │   ├── AdminDAOImpl.java          # Admin DAO implementation
│   │   ├── InstructorDAOImpl.java     # Instructor DAO implementation
│   │   ├── StudentDAOImpl.java        # Student DAO implementation
│   │   ├── CourseDAO.java             # Course DAO interface
│   │   ├── CourseDAOImpl.java         # Course DAO implementation
│   │   ├── QuizDAO.java               # Quiz DAO interface
│   │   ├── QuizDAOImpl.java           # Quiz DAO implementation
│   │   ├── QuestionDAO.java           # Question DAO interface
│   │   ├── QuestionDAOImpl.java       # Question DAO implementation
│   │   ├── QuizResultDAO.java         # Quiz result DAO interface
│   │   ├── QuizResultDAOImpl.java     # Quiz result DAO implementation
│   │   ├── EnrollmentDAO.java         # Enrollment DAO interface
│   │   └── EnrollmentDAOImpl.java     # Enrollment DAO implementation
│   │
│   ├── service/                       # Business logic layer
│   │   ├── AdminService.java          # Admin service interface
│   │   ├── AdminServiceImpl.java      # Admin service implementation
│   │   ├── InstructorService.java     # Instructor service interface
│   │   ├── InstructorServiceImpl.java # Instructor service implementation
│   │   ├── StudentService.java        # Student service interface
│   │   ├── StudentServiceImpl.java    # Student service implementation
│   │   ├── CourseService.java         # Course service interface
│   │   ├── CourseServiceImpl.java     # Course service implementation
│   │   ├── QuizService.java           # Quiz service interface
│   │   ├── QuizServiceImpl.java       # Quiz service implementation
│   │   ├── QuizResultService.java     # Quiz result service interface
│   │   └── QuizResultServiceImpl.java # Quiz result service implementation
│   │
│   └── utils/                         # Utility classes
│       └── DatabaseConnection.java    # Database connection singleton
│
├── target/                            # Compiled classes
├── pom.xml                            # Maven configuration
├── ums.sql                            # Database schema script
├── .env                               # Environment variables (create this)
├── LICENSE                            # Project license
└── README.md                          # Project documentation
```

---

## 🎨 Key Components

### 1. **Model Layer** (`model/`)
- Contains all entity classes representing database tables
- Implements inheritance hierarchy (User → Admin/Instructor/Student)
- Includes `toString()` methods for debugging

### 2. **DAO Layer** (`dao/`)
- Handles all database operations
- Implements CRUD operations for each entity
- Uses PreparedStatements to prevent SQL injection

### 3. **Service Layer** (`service/`)
- Contains business logic
- Acts as intermediary between presentation and DAO layers
- Implements validation and error handling

### 4. **Utils** (`utils/`)
- `DatabaseConnection`: Singleton pattern for DB connection management
- Loads credentials from `.env` file

### 5. **Main Application** (`Main.java`)
- Console-based user interface
- Role-based menu system
- Authentication and session management

---

## 🔧 Configuration

### Database Configuration

Edit your `.env` file:

```env
DB_URL=jdbc:mariadb://localhost:3306/UMS_DB
DB_USER=root
DB_PASSWORD=yourpassword
```

### Maven Dependencies

Key dependencies in `pom.xml`:

```xml
<dependencies>
    <!-- MariaDB JDBC Driver -->
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <version>3.3.0</version>
    </dependency>
    
    <!-- SLF4J Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
    
    <!-- Dotenv for environment variables -->
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

### How to Contribute

1. **Fork the Project**
2. **Create your Feature Branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your Changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the Branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Coding Standards

- Follow Java naming conventions
- Write meaningful commit messages
- Add comments for complex logic
- Update documentation for new features
- Write unit tests where applicable

---

## 🐛 Known Issues & Future Enhancements

### Current Limitations
- Console-based interface only (no GUI)
- Plain text password storage (needs encryption)
- No email verification system
- Limited input validation

### Planned Features
- 🔐 Password encryption (BCrypt)
- 📧 Email notification system
- 📊 Graphical reports and analytics
- 🖥️ JavaFX/Swing GUI interface
- 📱 RESTful API for mobile integration
- 🔍 Advanced search and filtering
- 📅 Academic calendar integration
- 💬 Messaging system between users

---

## 📄 License

Distributed under the MIT License. See `LICENSE` file for more information.

---

## 👥 Authors

- **Marwan Weal** - [@MaroWael](https://github.com/MaroWael)
- **Islam Ali** - [@IslamAli-0](https://github.com/IslamAli-0)
- **Abdulrahman Saeed** - [@AbdelrahmanSaid00](https://github.com/AbdelrahmanSaid00)

Project Link: [https://github.com/MaroWael/AIM-UMS](https://github.com/yourusername/AIM-UMS)

---

## 🙏 Acknowledgments

- [Maven](https://maven.apache.org/) - Dependency Management
- [MariaDB](https://mariadb.org/) - Database System
- [Java](https://www.oracle.com/java/) - Programming Language
- [Shields.io](https://shields.io/) - README Badges
- [Choose an Open Source License](https://choosealicense.com) - License Information

---

<div align="center">

### ⭐ If you find this project useful, please consider giving it a star!

**Made with ❤️ and ☕**

[⬆ Back to Top](#-university-management-system-ums)

</div>
