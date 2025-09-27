// src/main/resources/data.sql (Optional - Sample Data)
-- Sample todos for development
INSERT INTO todos (id, title, description, completed, priority, category, due_date, created_at, updated_at)
VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Complete Project Setup', 'Set up Spring Boot backend with PostgreSQL', false, 'HIGH', 'WORK', '2024-12-25', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001', 'Buy Groceries', 'Get vegetables and fruits from supermarket', false, 'MEDIUM', 'SHOPPING', '2024-12-20', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'Morning Workout', 'Complete 30-minute cardio session', true, 'HIGH', 'HEALTH', '2024-12-18', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Read Book', 'Continue reading "Clean Architecture"', false, 'LOW', 'PERSONAL', '2024-12-30', NOW(), NOW());

-- Sample tags

-- Insert sample calendar events (Updated for September 2025)
INSERT INTO calendar_events (id, title, description, start_time, end_time, category, priority, location, color, is_all_day, created_at, updated_at) VALUES
-- Today's events (September 24, 2025)
('event-001', 'Team Standup Meeting', 'Daily team sync and planning meeting', '2025-09-24 09:00:00', '2025-09-24 09:30:00', 'WORK', 'HIGH', 'Conference Room A', '#3b82f6', false, NOW(), NOW()),
('event-002', 'Doctor Appointment', 'Annual health checkup', '2025-09-24 14:00:00', '2025-09-24 15:00:00', 'HEALTH', 'HIGH', 'City Medical Center', '#ef4444', false, NOW(), NOW()),

-- Yesterday's events (September 23, 2025) - for overdue testing
('event-003', 'Client Call', 'Follow up on project status', '2025-09-23 16:00:00', '2025-09-23 17:00:00', 'WORK', 'HIGH', 'Office', '#3b82f6', false, NOW(), NOW()),

-- Tomorrow's events (September 25, 2025)
('event-004', 'Lunch with Sarah', 'Catch up over lunch', '2025-09-25 12:30:00', '2025-09-25 14:00:00', 'SOCIAL', 'MEDIUM', 'Downtown Cafe', '#f59e0b', false, NOW(), NOW()),
('event-005', 'Project Review', 'Quarterly project review meeting with stakeholders', '2025-09-25 10:00:00', '2025-09-25 12:00:00', 'WORK', 'HIGH', 'Zoom Meeting', '#3b82f6', false, NOW(), NOW()),

-- This week's events
('event-006', 'Gym Workout', 'Personal training session', '2025-09-26 07:00:00', '2025-09-26 08:30:00', 'PERSONAL', 'MEDIUM', 'FitLife Gym', '#10b981', false, NOW(), NOW()),
('event-007', 'Birthday Party', 'Alex birthday celebration', '2025-09-27 18:00:00', '2025-09-27 23:00:00', 'SOCIAL', 'LOW', 'Alex House', '#f59e0b', false, NOW(), NOW()),
('event-008', 'Weekend Getaway', 'Mini vacation to the mountains', '2025-09-28 00:00:00', '2025-09-29 23:59:59', 'PERSONAL', 'LOW', 'Mountain Resort', '#10b981', true, NOW(), NOW()),

-- Next week's events
('event-009', 'Code Review Session', 'Review pull requests and discuss architecture', '2025-10-01 15:00:00', '2025-10-01 16:30:00', 'WORK', 'MEDIUM', 'Office', '#3b82f6', false, NOW(), NOW()),
('event-010', 'Grocery Shopping', 'Weekly grocery shopping', '2025-10-02 10:00:00', '2025-10-02 11:30:00', 'OTHER', 'LOW', 'SuperMart', '#8b5cf6', false, NOW(), NOW()),
('event-011', 'Dentist Appointment', 'Regular dental cleaning', '2025-10-03 16:00:00', '2025-10-03 17:00:00', 'HEALTH', 'MEDIUM', 'Smile Dental Clinic', '#ef4444', false, NOW(), NOW()),

-- Future events
('event-012', 'Client Presentation', 'Present Q4 results to client', '2025-10-05 14:00:00', '2025-10-05 15:30:00', 'WORK', 'HIGH', 'Client Office', '#3b82f6', false, NOW(), NOW()),
('event-013', 'Book Club Meeting', 'Monthly book discussion', '2025-10-10 19:00:00', '2025-10-10 21:00:00', 'SOCIAL', 'LOW', 'Local Library', '#f59e0b', false, NOW(), NOW());

-- Insert attendees for some events
INSERT INTO event_attendees (event_id, attendee) VALUES
('event-001', 'john.doe@company.com'),
('event-001', 'jane.smith@company.com'),
('event-001', 'mike.wilson@company.com'),
('event-003', 'sarah.johnson@email.com'),
('event-004', 'john.doe@company.com'),
('event-004', 'jane.smith@company.com'),
('event-004', 'client@external.com'),
('event-004', 'manager@company.com'),
('event-006', 'alex@email.com'),
('event-006', 'friends@email.com'),
('event-007', 'john.doe@company.com'),
('event-007', 'senior.dev@company.com'),
('event-011', 'client.manager@client.com'),
('event-011', 'sales.rep@company.com'),
('event-012', 'bookclub.member1@email.com'),
('event-012', 'bookclub.member2@email.com');