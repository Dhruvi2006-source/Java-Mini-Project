-- ============================================================
-- Campus Event Management System Database Setup Script
-- Database: campus_event_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_event_db;
USE campus_event_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Events Table (Supports INDIVIDUAL and TEAM events)
CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    venue VARCHAR(150) NOT NULL,
    capacity INT NOT NULL DEFAULT 100,
    event_type ENUM('INDIVIDUAL', 'TEAM') NOT NULL DEFAULT 'INDIVIDUAL',
    min_team_size INT NOT NULL DEFAULT 1,
    max_team_size INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Registrations Table
CREATE TABLE IF NOT EXISTS registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_event UNIQUE (user_id, event_id),
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Teams Table
CREATE TABLE IF NOT EXISTS teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    team_name VARCHAR(100) NOT NULL,
    team_leader_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_team_leader FOREIGN KEY (team_leader_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_event_team_leader UNIQUE (event_id, team_leader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Team Members Table
CREATE TABLE IF NOT EXISTS team_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    team_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_member_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_team_user UNIQUE (team_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Sample Data Insertion
-- ============================================================

-- Insert Default Administrator Account (admin@campus.edu / admin123)
INSERT INTO users (name, email, password, role) 
VALUES (
    'System Administrator', 
    'admin@campus.edu', 
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 
    'ADMIN'
) ON DUPLICATE KEY UPDATE password='240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9';

-- Sample Student Accounts for testing (password: student123)
INSERT INTO users (name, email, password, role) 
VALUES 
('Demo Student', 'student@campus.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT'),
('Bhakti Gohil', 'bhakti@campus.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT'),
('Rahul Shah', 'rahul@campus.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT')
ON DUPLICATE KEY UPDATE password='703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b';

-- Insert Sample Events (Both Individual & Team Events)
INSERT INTO events (id, title, description, event_date, event_time, venue, capacity, event_type, min_team_size, max_team_size) VALUES 
(1, 'Annual Tech Symposium 2026', 'Join us for inspiring keynote sessions, project exhibitions, and coding competitions.', '2026-10-25', '09:30:00', 'Main Campus Auditorium', 150, 'INDIVIDUAL', 1, 1),
(2, 'Java Hackathon 2026 (Team Event)', 'A 24-hour coding challenge. Form a team of 2 to 4 students and build innovative Java web applications!', '2026-11-10', '09:00:00', 'IT Block - Main Lab', 40, 'TEAM', 2, 4),
(3, 'Campus Cultural Fest - Rhythm 2026', 'An evening of music, dance performances, drama, and food stalls celebrating student talent.', '2026-11-15', '17:00:00', 'Open Air Theatre (OAT)', 300, 'INDIVIDUAL', 1, 1),
(4, 'Inter-College Esports Tournament (Team Event)', 'Form a team of 3 to 5 players and compete in Valorant & EA FC 24 tournaments for cash prizes!', '2026-11-20', '11:00:00', 'Student Activity Center', 60, 'TEAM', 3, 5),
(5, 'Special Masterclass: AI & Ethics (Full Capacity)', 'Exclusive session with industry leaders on AI ethics and software engineering careers.', '2026-10-18', '14:00:00', 'Seminar Hall B', 2, 'INDIVIDUAL', 1, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title), event_type=VALUES(event_type), min_team_size=VALUES(min_team_size), max_team_size=VALUES(max_team_size);
