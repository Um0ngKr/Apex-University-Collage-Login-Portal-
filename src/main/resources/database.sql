-- =====================================================================
-- APEX UNIVERSITY OF TECHNOLOGY (APEX)
-- Complete University Examination & Academic Management System
-- Normalized MySQL Database Schema & Fully Verified BCrypt Seed Data
-- Database: apex_university_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `apex_university_db`
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `apex_university_db`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `login_history`;
DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `helpdesk_contacts`;
DROP TABLE IF EXISTS `instructions`;
DROP TABLE IF EXISTS `notices`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `results`;
DROP TABLE IF EXISTS `marks`;
DROP TABLE IF EXISTS `attendance`;
DROP TABLE IF EXISTS `exam_forms`;
DROP TABLE IF EXISTS `exam_schedules`;
DROP TABLE IF EXISTS `examinations`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `teacher_assignments`;
DROP TABLE IF EXISTS `teachers`;
DROP TABLE IF EXISTS `subjects`;
DROP TABLE IF EXISTS `semesters`;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `academic_sessions`;
DROP TABLE IF EXISTS `colleges`;
DROP TABLE IF EXISTS `departments`;
DROP TABLE IF EXISTS `admins`;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. Table: admins
-- ---------------------------------------------------------------------
CREATE TABLE `admins` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 2. Table: departments
-- ---------------------------------------------------------------------
CREATE TABLE `departments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `dept_code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `description` TEXT,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 3. Table: colleges
-- ---------------------------------------------------------------------
CREATE TABLE `colleges` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `college_code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(200) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `address` TEXT NOT NULL,
    `website` VARCHAR(150),
    `principal_name` VARCHAR(100),
    `password_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 4. Table: academic_sessions
-- ---------------------------------------------------------------------
CREATE TABLE `academic_sessions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `session_code` VARCHAR(20) NOT NULL UNIQUE,
    `start_year` INT NOT NULL,
    `end_year` INT NOT NULL,
    `is_current` TINYINT(1) NOT NULL DEFAULT 0,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 5. Table: courses
-- ---------------------------------------------------------------------
CREATE TABLE `courses` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `department_id` INT NOT NULL,
    `course_code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `duration_years` INT NOT NULL DEFAULT 4,
    `total_semesters` INT NOT NULL DEFAULT 8,
    `degree_level` VARCHAR(50) NOT NULL DEFAULT 'Undergraduate',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_courses_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 6. Table: semesters
-- ---------------------------------------------------------------------
CREATE TABLE `semesters` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `course_id` INT NOT NULL,
    `semester_number` INT NOT NULL,
    `semester_name` VARCHAR(50) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_course_sem` (`course_id`, `semester_number`),
    CONSTRAINT `fk_semesters_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 7. Table: subjects
-- ---------------------------------------------------------------------
CREATE TABLE `subjects` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `course_id` INT NOT NULL,
    `semester_id` INT NOT NULL,
    `subject_code` VARCHAR(30) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `credit` DECIMAL(3,1) NOT NULL DEFAULT 4.0,
    `max_internal_marks` INT NOT NULL DEFAULT 30,
    `max_external_marks` INT NOT NULL DEFAULT 70,
    `pass_marks` INT NOT NULL DEFAULT 40,
    `subject_type` VARCHAR(30) NOT NULL DEFAULT 'THEORY',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_subjects_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_subjects_sem` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 8. Table: teachers
-- ---------------------------------------------------------------------
CREATE TABLE `teachers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `employee_id` VARCHAR(50) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `designation` VARCHAR(100) NOT NULL DEFAULT 'Assistant Professor',
    `department_id` INT NOT NULL,
    `qualification` VARCHAR(100),
    `password_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_teachers_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 9. Table: teacher_assignments
-- ---------------------------------------------------------------------
CREATE TABLE `teacher_assignments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `teacher_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `semester_id` INT NOT NULL,
    `subject_id` INT NOT NULL,
    `session_id` INT NOT NULL,
    `section` VARCHAR(10) NOT NULL DEFAULT 'A',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_teacher_assign` (`teacher_id`, `subject_id`, `session_id`, `section`),
    CONSTRAINT `fk_assign_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_assign_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_assign_sem` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_assign_sub` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_assign_ses` FOREIGN KEY (`session_id`) REFERENCES `academic_sessions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 10. Table: students
-- ---------------------------------------------------------------------
CREATE TABLE `students` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `registration_no` VARCHAR(50) NOT NULL UNIQUE,
    `roll_no` VARCHAR(50) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `dob` DATE,
    `gender` VARCHAR(10) DEFAULT 'Male',
    `blood_group` VARCHAR(10) DEFAULT 'O+',
    `father_name` VARCHAR(100),
    `mother_name` VARCHAR(100),
    `address` TEXT,
    `college_id` INT NOT NULL,
    `department_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `current_semester_id` INT NOT NULL,
    `session_id` INT NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `profile_image` VARCHAR(255) DEFAULT 'default-avatar.png',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_students_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_students_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_students_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_students_sem` FOREIGN KEY (`current_semester_id`) REFERENCES `semesters` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_students_ses` FOREIGN KEY (`session_id`) REFERENCES `academic_sessions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 11. Table: examinations
-- ---------------------------------------------------------------------
CREATE TABLE `examinations` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `exam_name` VARCHAR(150) NOT NULL,
    `exam_code` VARCHAR(50) NOT NULL UNIQUE,
    `session_id` INT NOT NULL,
    `semester_number` INT NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `exam_type` VARCHAR(50) NOT NULL DEFAULT 'REGULAR',
    `fee_amount` DECIMAL(10,2) NOT NULL DEFAULT 1200.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `is_result_published` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_exams_ses` FOREIGN KEY (`session_id`) REFERENCES `academic_sessions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 12. Table: exam_schedules
-- ---------------------------------------------------------------------
CREATE TABLE `exam_schedules` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `exam_id` INT NOT NULL,
    `subject_id` INT NOT NULL,
    `exam_date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `room_no` VARCHAR(50) DEFAULT 'Main Examination Hall',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_sched_exam` FOREIGN KEY (`exam_id`) REFERENCES `examinations` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sched_sub` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 13. Table: exam_forms
-- ---------------------------------------------------------------------
CREATE TABLE `exam_forms` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `exam_id` INT NOT NULL,
    `student_id` INT NOT NULL,
    `college_id` INT NOT NULL,
    `submission_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `college_approval_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `admin_approval_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `rejection_reason` VARCHAR(255),
    `admit_card_issued` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_student_exam_form` (`exam_id`, `student_id`),
    CONSTRAINT `fk_form_exam` FOREIGN KEY (`exam_id`) REFERENCES `examinations` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_form_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_form_college` FOREIGN KEY (`college_id`) REFERENCES `colleges` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 14. Table: attendance
-- ---------------------------------------------------------------------
CREATE TABLE `attendance` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `subject_id` INT NOT NULL,
    `session_id` INT NOT NULL,
    `class_date` DATE NOT NULL,
    `status` VARCHAR(10) NOT NULL DEFAULT 'PRESENT',
    `marked_by_teacher_id` INT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_student_sub_date` (`student_id`, `subject_id`, `class_date`),
    CONSTRAINT `fk_att_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_att_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_att_session` FOREIGN KEY (`session_id`) REFERENCES `academic_sessions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_att_teacher` FOREIGN KEY (`marked_by_teacher_id`) REFERENCES `teachers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 15. Table: marks
-- ---------------------------------------------------------------------
CREATE TABLE `marks` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `exam_id` INT NOT NULL,
    `student_id` INT NOT NULL,
    `subject_id` INT NOT NULL,
    `internal_marks` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `external_marks` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `total_marks` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `grade_point` DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    `letter_grade` VARCHAR(5) NOT NULL DEFAULT 'F',
    `is_pass` TINYINT(1) NOT NULL DEFAULT 0,
    `evaluated_by_teacher_id` INT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_mark_exam_student_sub` (`exam_id`, `student_id`, `subject_id`),
    CONSTRAINT `fk_marks_exam` FOREIGN KEY (`exam_id`) REFERENCES `examinations` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_marks_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_marks_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_marks_teacher` FOREIGN KEY (`evaluated_by_teacher_id`) REFERENCES `teachers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 16. Table: results
-- ---------------------------------------------------------------------
CREATE TABLE `results` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `exam_id` INT NOT NULL,
    `student_id` INT NOT NULL,
    `course_id` INT NOT NULL,
    `semester_number` INT NOT NULL,
    `session_id` INT NOT NULL,
    `total_obtained` DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    `total_max` DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    `percentage` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `sgpa` DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    `result_status` VARCHAR(20) NOT NULL DEFAULT 'PASS',
    `is_published` TINYINT(1) NOT NULL DEFAULT 0,
    `published_at` TIMESTAMP NULL DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_results_exam_student` (`exam_id`, `student_id`),
    CONSTRAINT `fk_results_exam` FOREIGN KEY (`exam_id`) REFERENCES `examinations` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_results_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_results_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_results_session` FOREIGN KEY (`session_id`) REFERENCES `academic_sessions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 17. Table: payments
-- ---------------------------------------------------------------------
CREATE TABLE `payments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `student_id` INT NOT NULL,
    `exam_id` INT,
    `exam_form_id` INT,
    `transaction_id` VARCHAR(100) NOT NULL UNIQUE,
    `amount` DECIMAL(10,2) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'SANDBOX_GATEWAY',
    `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    `receipt_no` VARCHAR(50) NOT NULL UNIQUE,
    `remarks` VARCHAR(255),
    `verified_by_admin_id` INT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_payments_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_payments_exam` FOREIGN KEY (`exam_id`) REFERENCES `examinations` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_payments_form` FOREIGN KEY (`exam_form_id`) REFERENCES `exam_forms` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_payments_admin` FOREIGN KEY (`verified_by_admin_id`) REFERENCES `admins` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 18. Table: notices
-- ---------------------------------------------------------------------
CREATE TABLE `notices` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `category` VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    `description` TEXT NOT NULL,
    `attachment_url` VARCHAR(255),
    `target_audience` VARCHAR(50) NOT NULL DEFAULT 'ALL',
    `is_published` TINYINT(1) NOT NULL DEFAULT 1,
    `published_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `created_by_admin_id` INT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notices_admin` FOREIGN KEY (`created_by_admin_id`) REFERENCES `admins` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 19. Table: instructions
-- ---------------------------------------------------------------------
CREATE TABLE `instructions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `category` VARCHAR(50) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `sort_order` INT NOT NULL DEFAULT 1,
    `is_published` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 20. Table: helpdesk_contacts
-- ---------------------------------------------------------------------
CREATE TABLE `helpdesk_contacts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `category` VARCHAR(50) NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `contact_person` VARCHAR(100),
    `phone_number` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `timing` VARCHAR(100) DEFAULT '10:00 AM - 05:00 PM (Mon-Sat)',
    `address` VARCHAR(255) DEFAULT 'Kaikhali Main Rd, Kolkata',
    `sort_order` INT NOT NULL DEFAULT 1,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 21. Table: audit_logs
-- ---------------------------------------------------------------------
CREATE TABLE `audit_logs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` VARCHAR(50) NOT NULL,
    `user_id` INT,
    `username` VARCHAR(100) NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `table_affected` VARCHAR(50),
    `record_id` INT,
    `details` TEXT,
    `ip_address` VARCHAR(50) DEFAULT '127.0.0.1',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- 22. Table: login_history
-- ---------------------------------------------------------------------
CREATE TABLE `login_history` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_type` VARCHAR(50) NOT NULL,
    `user_id` INT,
    `username` VARCHAR(100) NOT NULL,
    `ip_address` VARCHAR(50) DEFAULT '127.0.0.1',
    `user_agent` TEXT,
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =====================================================================
-- INITIAL CONFIGURABLE SEED DATA
-- Demonstration Credentials (BCrypt verified):
-- Admin:    admin / admin123  (or email: umangkr871170@gmail.com)
-- Teacher:  TCH101 / teacher123 (or email: rajesh.sharma@apex.edu)
-- Student:  APEX202200101 (or roll: 11500122001, or email: umangkr871170@gmail.com) / student123
-- College:  COL101 / college123 (or email: info@apextech.edu.in)
-- =====================================================================

-- 1. Admins
INSERT INTO `admins` (`id`, `username`, `password_hash`, `full_name`, `email`, `role`, `status`) VALUES
(1, 'admin', '$2a$12$oDQWHOAgAPYsjI8w0l277.8vy3nYXzwFvBAQwWU4BP8RXKScBlb/G', 'System Administrator', 'umangkr871170@gmail.com', 'ADMIN', 'ACTIVE'),
(2, 'admin_inactive', '$2a$12$oDQWHOAgAPYsjI8w0l277.8vy3nYXzwFvBAQwWU4BP8RXKScBlb/G', 'Inactive Admin', 'inactive_admin@apex.edu', 'ADMIN', 'INACTIVE');

-- 2. Departments
INSERT INTO `departments` (`id`, `dept_code`, `name`, `description`, `status`) VALUES
(1, 'CSE', 'Computer Science & Engineering', 'Department of Computer Science and Engineering', 'ACTIVE'),
(2, 'IT', 'Information Technology', 'Department of Information Technology', 'ACTIVE'),
(3, 'ECE', 'Electronics & Communication Engineering', 'Department of Electronics & Communication', 'ACTIVE'),
(4, 'CA', 'Computer Applications', 'Department of Computer Applications', 'ACTIVE');

-- 3. Academic Sessions
INSERT INTO `academic_sessions` (`id`, `session_code`, `start_year`, `end_year`, `is_current`, `status`) VALUES
(1, '2022-2026', 2022, 2026, 0, 'ACTIVE'),
(2, '2023-2027', 2023, 2027, 0, 'ACTIVE'),
(3, '2024-2028', 2024, 2028, 1, 'ACTIVE');

-- 4. Courses
INSERT INTO `courses` (`id`, `department_id`, `course_code`, `name`, `duration_years`, `total_semesters`, `degree_level`, `status`) VALUES
(1, 1, 'BTECH-CSE', 'Bachelor of Technology in Computer Science & Engineering', 4, 8, 'Undergraduate', 'ACTIVE'),
(2, 2, 'BTECH-IT', 'Bachelor of Technology in Information Technology', 4, 8, 'Undergraduate', 'ACTIVE'),
(3, 4, 'BCA', 'Bachelor of Computer Applications', 3, 6, 'Undergraduate', 'ACTIVE');

-- 5. Semesters for BTECH-CSE
INSERT INTO `semesters` (`id`, `course_id`, `semester_number`, `semester_name`, `status`) VALUES
(1, 1, 1, 'Semester 1', 'ACTIVE'),
(2, 1, 2, 'Semester 2', 'ACTIVE'),
(3, 1, 3, 'Semester 3', 'ACTIVE'),
(4, 1, 4, 'Semester 4', 'ACTIVE'),
(5, 1, 5, 'Semester 5', 'ACTIVE'),
(6, 1, 6, 'Semester 6', 'ACTIVE'),
(7, 1, 7, 'Semester 7', 'ACTIVE'),
(8, 1, 8, 'Semester 8', 'ACTIVE'),
-- Semesters for BTECH-IT
(9, 2, 1, 'Semester 1', 'ACTIVE'),
(10, 2, 2, 'Semester 2', 'ACTIVE'),
(11, 2, 3, 'Semester 3', 'ACTIVE'),
(12, 2, 4, 'Semester 4', 'ACTIVE'),
-- Semesters for BCA
(13, 3, 1, 'Semester 1', 'ACTIVE'),
(14, 3, 2, 'Semester 2', 'ACTIVE'),
(15, 3, 3, 'Semester 3', 'ACTIVE'),
(16, 3, 4, 'Semester 4', 'ACTIVE');

-- 6. Subjects for BTECH-CSE Semester 4 (Semester ID = 4)
INSERT INTO `subjects` (`id`, `course_id`, `semester_id`, `subject_code`, `name`, `credit`, `max_internal_marks`, `max_external_marks`, `pass_marks`, `subject_type`, `status`) VALUES
(1, 1, 4, 'CS401', 'Discrete Mathematics & Graph Theory', 4.0, 30, 70, 40, 'THEORY', 'ACTIVE'),
(2, 1, 4, 'CS402', 'Design & Analysis of Algorithms', 4.0, 30, 70, 40, 'THEORY', 'ACTIVE'),
(3, 1, 4, 'CS403', 'Database Management Systems', 4.0, 30, 70, 40, 'THEORY', 'ACTIVE'),
(4, 1, 4, 'CS404', 'Formal Language & Automata Theory', 3.0, 30, 70, 40, 'THEORY', 'ACTIVE'),
(5, 1, 4, 'CS492', 'Algorithms Laboratory', 2.0, 40, 60, 40, 'PRACTICAL', 'ACTIVE'),
(6, 1, 4, 'CS493', 'DBMS Laboratory', 2.0, 40, 60, 40, 'PRACTICAL', 'ACTIVE');

-- 7. Affiliated Colleges
INSERT INTO `colleges` (`id`, `college_code`, `name`, `email`, `phone`, `address`, `website`, `principal_name`, `password_hash`, `status`) VALUES
(1, 'COL101', 'Apex Institute of Technology & Management', 'info@apextech.edu.in', '08709878369', 'Kaikhali Main Rd, Campus 1, Kolkata', 'http://apextech.edu.in', 'Dr. S. K. Mukherjee', '$2a$12$XGYa1caHdrE9hC7X01IW9.USl3we1yTibi.UEU/HTJmCjOFDz9mVC', 'ACTIVE'),
(2, 'COL102', 'Apex City Engineering College', 'contact@apexcity.edu.in', '7667046114', 'Sector V, Salt Lake City, Kolkata', 'http://apexcity.edu.in', 'Dr. Alok Verma', '$2a$12$XGYa1caHdrE9hC7X01IW9.USl3we1yTibi.UEU/HTJmCjOFDz9mVC', 'ACTIVE'),
(3, 'COL103', 'Apex Global Academy of Engineering', 'office@apexglobal.edu.in', '8709878369', 'Rajarhat New Town, Action Area II, Kolkata', 'http://apexglobal.edu.in', 'Dr. Meenakshi Banerjee', '$2a$12$XGYa1caHdrE9hC7X01IW9.USl3we1yTibi.UEU/HTJmCjOFDz9mVC', 'ACTIVE'),
(4, 'COL_INACTIVE', 'Apex Inactive College of Technology', 'inactive.college@apex.edu', '7667046114', 'Kaikhali Main Rd, Kolkata', 'http://inactive.apex.edu', 'Dr. Inactive Principal', '$2a$12$XGYa1caHdrE9hC7X01IW9.USl3we1yTibi.UEU/HTJmCjOFDz9mVC', 'INACTIVE');

-- 8. Teachers
INSERT INTO `teachers` (`id`, `employee_id`, `full_name`, `email`, `phone`, `designation`, `department_id`, `qualification`, `password_hash`, `status`) VALUES
(1, 'TCH101', 'Prof. Rajesh Sharma', 'rajesh.sharma@apex.edu', '8709878369', 'Associate Professor', 1, 'Ph.D. in Computer Science', '$2a$12$msEvlTdgTfMXt7Vr7pBYc.gYWKl.7u/mr7xU0C4rLnHi0liTXtEzG', 'ACTIVE'),
(2, 'TCH102', 'Dr. Ananya Sen', 'ananya.sen@apex.edu', '7667046114', 'Professor & HOD', 1, 'Ph.D. from IIT Kharagpur', '$2a$12$msEvlTdgTfMXt7Vr7pBYc.gYWKl.7u/mr7xU0C4rLnHi0liTXtEzG', 'ACTIVE'),
(3, 'TCH103', 'Prof. Vikram Malhotra', 'vikram.m@apex.edu', '9508153596', 'Assistant Professor', 2, 'M.Tech in Information Technology', '$2a$12$msEvlTdgTfMXt7Vr7pBYc.gYWKl.7u/mr7xU0C4rLnHi0liTXtEzG', 'ACTIVE'),
(4, 'TCH_INACTIVE', 'Prof. Inactive Teacher', 'inactive.teacher@apex.edu', '8709878369', 'Assistant Professor', 1, 'M.Tech', '$2a$12$msEvlTdgTfMXt7Vr7pBYc.gYWKl.7u/mr7xU0C4rLnHi0liTXtEzG', 'INACTIVE');

-- 9. Teacher Assignments
INSERT INTO `teacher_assignments` (`id`, `teacher_id`, `course_id`, `semester_id`, `subject_id`, `session_id`, `section`) VALUES
(1, 1, 1, 4, 2, 1, 'A'), -- Rajesh Sharma -> Design & Analysis of Algorithms
(2, 1, 1, 4, 5, 1, 'A'), -- Rajesh Sharma -> Algorithms Lab
(3, 2, 1, 4, 3, 1, 'A'), -- Ananya Sen -> Database Management Systems
(4, 2, 1, 4, 6, 1, 'A'), -- Ananya Sen -> DBMS Lab
(5, 3, 1, 4, 1, 1, 'A'); -- Vikram Malhotra -> Discrete Mathematics

-- 10. Students
INSERT INTO `students` (`id`, `registration_no`, `roll_no`, `full_name`, `email`, `phone`, `dob`, `gender`, `blood_group`, `father_name`, `mother_name`, `address`, `college_id`, `department_id`, `course_id`, `current_semester_id`, `session_id`, `password_hash`, `status`) VALUES
(1, 'APEX202200101', '11500122001', 'Rahul Kumar', 'umangkr871170@gmail.com', '8709878369', '2004-05-15', 'Male', 'O+', 'Surendra Kumar', 'Anita Devi', 'Kaikhali Main Rd, Kolkata', 1, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'ACTIVE'),
(2, 'APEX202200102', '11500122002', 'Sneha Roy', 'sneha.roy@student.apex.edu', '7667046114', '2004-08-22', 'Female', 'B+', 'Prabir Roy', 'Mala Roy', 'Salt Lake Sector 3, Kolkata', 1, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'ACTIVE'),
(3, 'APEX202200103', '11500122003', 'Amit Patel', 'amit.patel@student.apex.edu', '9508153596', '2003-11-10', 'Male', 'A+', 'Dineshbhai Patel', 'Kokilaben Patel', 'New Town Action Area 1, Kolkata', 2, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'ACTIVE'),
(4, 'APEX202200104', '11500122004', 'Priya Singh', 'priya.singh@student.apex.edu', '7061425471', '2004-01-30', 'Female', 'AB+', 'Rajesh Singh', 'Sunita Singh', 'VIP Road, Teghoria, Kolkata', 3, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'ACTIVE'),
(5, 'APEX202200105', '11500122005', 'Rohan Das', 'rohan.das@student.apex.edu', '7320991072', '2004-09-12', 'Male', 'O-', 'Bimal Das', 'Kalpana Das', 'Baguiati Main Road, Kolkata', 1, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'ACTIVE'),
(6, 'APEX_INACTIVE', '11500122999', 'Inactive Student', 'inactive.student@apex.edu', '8709878369', '2004-01-01', 'Male', 'O+', 'Father Name', 'Mother Name', 'Kolkata', 1, 1, 1, 4, 1, '$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq', 'INACTIVE');

-- 11. Examinations
INSERT INTO `examinations` (`id`, `exam_name`, `exam_code`, `session_id`, `semester_number`, `start_date`, `end_date`, `exam_type`, `fee_amount`, `status`, `is_result_published`) VALUES
(1, 'B.Tech Even Semester End Examination 2024', 'EXAM2024EVEN', 1, 4, '2024-06-10', '2024-06-25', 'REGULAR', 1200.00, 'ACTIVE', 1),
(2, 'B.Tech Odd Semester End Examination 2024', 'EXAM2024ODD', 1, 5, '2024-12-05', '2024-12-20', 'REGULAR', 1200.00, 'ACTIVE', 0);

-- 12. Exam Schedules for Exam 1
INSERT INTO `exam_schedules` (`id`, `exam_id`, `subject_id`, `exam_date`, `start_time`, `end_time`, `room_no`) VALUES
(1, 1, 1, '2024-06-10', '10:00:00', '13:00:00', 'Examination Hall 101'),
(2, 1, 2, '2024-06-13', '10:00:00', '13:00:00', 'Examination Hall 101'),
(3, 1, 3, '2024-06-16', '10:00:00', '13:00:00', 'Examination Hall 102'),
(4, 1, 4, '2024-06-19', '10:00:00', '13:00:00', 'Examination Hall 102'),
(5, 1, 5, '2024-06-22', '14:00:00', '17:00:00', 'Algorithms Lab Block C'),
(6, 1, 6, '2024-06-25', '14:00:00', '17:00:00', 'Database Lab Block C');

-- 13. Exam Forms
INSERT INTO `exam_forms` (`id`, `exam_id`, `student_id`, `college_id`, `submission_date`, `college_approval_status`, `admin_approval_status`, `payment_status`, `rejection_reason`, `admit_card_issued`) VALUES
(1, 1, 1, 1, '2024-05-10 11:20:00', 'APPROVED', 'APPROVED', 'SUCCESS', NULL, 1),
(2, 1, 2, 1, '2024-05-11 14:15:00', 'APPROVED', 'APPROVED', 'SUCCESS', NULL, 1),
(3, 1, 3, 2, '2024-05-12 16:45:00', 'APPROVED', 'APPROVED', 'SUCCESS', NULL, 1),
(4, 1, 4, 3, '2024-05-13 10:05:00', 'APPROVED', 'PENDING', 'SUCCESS', NULL, 0),
(5, 1, 5, 1, '2024-05-14 12:30:00', 'PENDING', 'PENDING', 'PENDING', NULL, 0);

-- 14. Attendance Records for Student 1 (Rahul Kumar)
INSERT INTO `attendance` (`student_id`, `subject_id`, `session_id`, `class_date`, `status`, `marked_by_teacher_id`) VALUES
(1, 1, 1, '2024-04-01', 'PRESENT', 3),
(1, 1, 1, '2024-04-03', 'PRESENT', 3),
(1, 1, 1, '2024-04-08', 'PRESENT', 3),
(1, 1, 1, '2024-04-10', 'ABSENT', 3),
(1, 2, 1, '2024-04-02', 'PRESENT', 1),
(1, 2, 1, '2024-04-04', 'PRESENT', 1),
(1, 2, 1, '2024-04-09', 'PRESENT', 1),
(1, 3, 1, '2024-04-01', 'PRESENT', 2),
(1, 3, 1, '2024-04-05', 'PRESENT', 2),
(1, 3, 1, '2024-04-08', 'PRESENT', 2);

-- 15. Marks for Rahul Kumar (Student 1) in Exam 1
INSERT INTO `marks` (`exam_id`, `student_id`, `subject_id`, `internal_marks`, `external_marks`, `total_marks`, `grade_point`, `letter_grade`, `is_pass`, `evaluated_by_teacher_id`) VALUES
(1, 1, 1, 27.00, 61.00, 88.00, 9.00, 'A+', 1, 3),
(1, 1, 2, 28.50, 64.00, 92.50, 10.00, 'O', 1, 1),
(1, 1, 3, 26.00, 59.00, 85.00, 9.00, 'A+', 1, 2),
(1, 1, 4, 25.00, 55.00, 80.00, 9.00, 'A+', 1, 1),
(1, 1, 5, 38.00, 56.00, 94.00, 10.00, 'O', 1, 1),
(1, 1, 6, 37.00, 55.00, 92.00, 10.00, 'O', 1, 2),
-- Sneha Roy (Student 2)
(1, 2, 1, 25.00, 58.00, 83.00, 9.00, 'A+', 1, 3),
(1, 2, 2, 26.00, 60.00, 86.00, 9.00, 'A+', 1, 1),
(1, 2, 3, 24.00, 54.00, 78.00, 8.00, 'A', 1, 2),
(1, 2, 4, 23.00, 52.00, 75.00, 8.00, 'A', 1, 1),
(1, 2, 5, 36.00, 52.00, 88.00, 9.00, 'A+', 1, 1),
(1, 2, 6, 35.00, 53.00, 88.00, 9.00, 'A+', 1, 2);

-- 16. Results (Compiled & Published for Exam 1)
INSERT INTO `results` (`id`, `exam_id`, `student_id`, `course_id`, `semester_number`, `session_id`, `total_obtained`, `total_max`, `percentage`, `sgpa`, `result_status`, `is_published`, `published_at`) VALUES
(1, 1, 1, 1, 4, 1, 531.50, 600.00, 88.58, 9.45, 'PASS', 1, '2024-07-05 15:00:00'),
(2, 1, 2, 1, 4, 1, 498.00, 600.00, 83.00, 8.85, 'PASS', 1, '2024-07-05 15:00:00');

-- 17. Payments
INSERT INTO `payments` (`id`, `student_id`, `exam_id`, `exam_form_id`, `transaction_id`, `amount`, `payment_method`, `payment_date`, `payment_status`, `receipt_no`, `remarks`, `verified_by_admin_id`) VALUES
(1, 1, 1, 1, 'TXN-APEX-20240510-871170', 1200.00, 'SANDBOX_UPI', '2024-05-10 11:22:15', 'SUCCESS', 'RCPT-APEX-2024-00101', 'Semester 4 Regular Exam Form Fee', 1),
(2, 2, 1, 2, 'TXN-APEX-20240511-766704', 1200.00, 'SANDBOX_NETBANKING', '2024-05-11 14:18:40', 'SUCCESS', 'RCPT-APEX-2024-00102', 'Semester 4 Regular Exam Form Fee', 1),
(3, 3, 1, 3, 'TXN-APEX-20240512-950815', 1200.00, 'SANDBOX_DEBIT_CARD', '2024-05-12 16:50:00', 'SUCCESS', 'RCPT-APEX-2024-00103', 'Semester 4 Regular Exam Form Fee', 1);

-- 18. Dynamic Notices & Announcements
INSERT INTO `notices` (`id`, `title`, `category`, `description`, `attachment_url`, `target_audience`, `is_published`, `published_at`, `created_by_admin_id`) VALUES
(1, 'Publication of B.Tech Even Semester End Examination 2024 Results', 'EXAMINATION', 'The Controller of Examinations is pleased to announce that results for B.Tech Even Semester End Examination 2024 have been published. Students can view and download their marksheet from the Result Portal.', 'results_notice_2024.pdf', 'ALL', 1, '2024-07-05 15:30:00', 1),
(2, 'Admit Card Available for Download - Even Semester Examination', 'EXAMINATION', 'All eligible students who have submitted their exam forms and completed payment can now download their digital Admit Cards from their Student Portal.', 'admit_card_notice.pdf', 'STUDENTS', 1, '2024-05-25 10:00:00', 1),
(3, 'Notice regarding Form Fill-up for Odd Semester End Examination 2024', 'ACADEMIC', 'Form fill-up portal for Odd Semester End Examination will remain open until November 25, 2024. Colleges and students are requested to complete verifications strictly within the deadline.', 'odd_sem_form_schedule.pdf', 'ALL', 1, '2024-11-01 09:30:00', 1),
(4, 'Important Guidelines on Malpractice Prevention and Hall Protocols', 'ADMINISTRATIVE', 'Strict adherence to university examination hall protocols is mandatory. Mobile phones, programmable calculators, and unauthorized materials are completely prohibited inside examination halls.', NULL, 'ALL', 1, '2024-06-01 11:00:00', 1);

-- 19. Initial Configurable Instructions (5 Initial Categories Required)
INSERT INTO `instructions` (`id`, `category`, `title`, `content`, `sort_order`, `is_published`) VALUES
(1, 'EXAM_FORM', 'Exam Form Filling Instructions', '1. Login to your Student Portal using your Registration Number / Roll Number and Password.\n2. Navigate to Examination > Fill Exam Form from the menu.\n3. Verify all personal details, enrolled subjects, and fee amount.\n4. Click Submit Exam Form to forward your application to your affiliated College for verification.\n5. Upon College verification, proceed to the Fee Payment tab to complete payment.', 1, 1),
(2, 'PAYMENT', 'Online Fee Payment Instructions', '1. Select Make Payment after your exam form is approved by your College.\n2. In Sandbox mode, choose your simulated payment method (UPI / Net Banking / Debit Card).\n3. Click Pay Now. Do NOT refresh or close the browser window during processing.\n4. Save and download the Electronic Fee Payment Receipt immediately upon success.\n5. For payment failure or double-debit, contact the Payment Helpline: 8709878369 or email adarshraj112008@gmail.com.', 2, 1),
(3, 'ADMIT_CARD', 'Admit Card Download Instructions', '1. Digital Admit Cards are issued only after College Approval, Admin Approval, and Fee Payment clearance.\n2. Go to Student Dashboard > Examination > Download Admit Card.\n3. Verify your examination center, scheduled dates, and timings.\n4. Print a copy of your Admit Card and bring it along with a valid College Photo ID to each examination session.', 3, 1),
(4, 'RESULT', 'Result Checking Instructions', '1. Go to the public Result Portal on the Apex University homepage.\n2. Select your Examination, Academic Session, and Semester.\n3. Enter your 11-digit Registration Number or 11-digit Roll Number.\n4. Click Search Result to display your Grade Card with subject-wise marks, percentage, and SGPA.\n5. Click Download Marksheet PDF for official offline record keeping.', 4, 1),
(5, 'REGISTRATION', 'Registration Instructions', '1. Newly admitted candidates will receive their permanent Registration Number from their affiliated College.\n2. Keep your unique Registration ID secure; it serves as your username throughout your academic tenure.\n3. Any discrepancy in student profile information must be reported through the College Portal within 14 days of enrollment.', 5, 1);

-- 20. Exact HelpDesk Details (Preserved verbatim as requested)
INSERT INTO `helpdesk_contacts` (`id`, `category`, `title`, `contact_person`, `phone_number`, `email`, `timing`, `address`, `sort_order`, `status`) VALUES
(1, 'PAYMENT', 'Online Payment Issues', 'Accounts & Finance Officer', '8709878369', 'adarshraj112008@gmail.com', '24/7 Dedicated Payment Support', 'Kaikhali Main Rd, Kolkata', 1, 'ACTIVE'),
(2, 'PAYMENT', '24/7 Customer Care (Payment & General)', 'Helpdesk Support Team', '7667046114', 'adarshraj112008@gmail.com', 'Round the Clock 24/7', 'Kaikhali Main Rd, Kolkata', 2, 'ACTIVE'),
(3, 'TECHNICAL', 'Technical Helpdesk', 'IT & Systems Cell', '08709878369', 'umangkr87170@gmail.com', '09:00 AM - 07:00 PM (Mon-Sat)', 'Apex IT Center, Kaikhali Main Rd', 3, 'ACTIVE'),
(4, 'TECHNICAL', 'Examination Portal Support', 'Technical Coordinator', '8709878369', 'umangkr87170@gmail.com', '09:00 AM - 06:00 PM (Mon-Sat)', 'Apex IT Center, Kaikhali Main Rd', 4, 'ACTIVE'),
(5, 'COE', 'Controller of Examinations / Transcripts / Duplicate Documents', 'COE Office', '95081 53596', 'umangkr871170@gmail.com', '10:00 AM - 05:00 PM (Mon-Fri)', 'Controller of Examinations Division, Kaikhali Main Rd', 5, 'ACTIVE'),
(6, 'REGISTRATION', 'Registration & Migration Cell', 'Registrar Office', '70614 25471', 'umangkr871170@gmail.com', '10:00 AM - 05:00 PM (Mon-Fri)', 'Administrative Block, Kaikhali Main Rd', 6, 'ACTIVE'),
(7, 'CERTIFICATE', 'Transcript / Certificate Support', 'Certification Cell', '7320 991 072', 'umangkr871170@gmail.com', '10:00 AM - 05:00 PM (Mon-Fri)', 'Academic Section, Kaikhali Main Rd', 7, 'ACTIVE'),
(8, 'GENERAL', 'General Enquiry & Campus Reception', 'Public Relations Office', '7667046114', 'umangkr871170@gmail.com', '09:00 AM - 06:00 PM (Mon-Sat)', 'Apex University Campus, Kaikhali Main Rd', 8, 'ACTIVE');

-- 21. Initial Audit Log Entry
INSERT INTO `audit_logs` (`user_type`, `user_id`, `username`, `action`, `table_affected`, `record_id`, `details`, `ip_address`) VALUES
('SYSTEM', 1, 'admin', 'SYSTEM_INITIALIZATION', 'all', 1, 'Database initialized with full schema and verified BCrypt seed data for Apex University of Technology.', '127.0.0.1');
