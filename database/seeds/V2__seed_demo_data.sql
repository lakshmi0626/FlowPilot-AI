-- ============================================================
-- FlowPilot AI - Commercial Seed Data
-- Demo Tenants: Acme Corporation & TechFlow Innovations
-- ============================================================

-- Seed Plans
INSERT INTO plans (id, name, code, price_monthly, price_yearly, max_users, max_workflows, max_executions_per_month, features_json) VALUES
(1, 'Free Trial', 'TRIAL', 0.00, 0.00, 5, 3, 100, '{"aiAssistant":true,"visualBuilder":true,"webhooks":false,"sla":false}'),
(2, 'Starter', 'STARTER', 49.00, 490.00, 15, 10, 1000, '{"aiAssistant":true,"visualBuilder":true,"webhooks":true,"sla":false}'),
(3, 'Professional', 'PROFESSIONAL', 149.00, 1490.00, 50, 50, 10000, '{"aiAssistant":true,"visualBuilder":true,"webhooks":true,"sla":true,"prioritySupport":true}'),
(4, 'Enterprise', 'ENTERPRISE', 399.00, 3990.00, 500, 500, 100000, '{"aiAssistant":true,"visualBuilder":true,"webhooks":true,"sla":true,"customIntegrations":true,"dedicatedAccountManager":true}');

-- Seed Organizations
INSERT INTO organizations (id, name, slug, domain, logo_url, primary_color, secondary_color, active) VALUES
(1, 'Acme Corporation', 'acme-corp', 'acme.example.com', 'https://images.unsplash.com/photo-1572021335469-31706a17aaef?w=150', '#16A36A', '#174C3C', TRUE),
(2, 'TechFlow Innovations', 'techflow', 'techflow.example.com', 'https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=150', '#16A36A', '#174C3C', TRUE);

-- Seed Subscriptions
INSERT INTO subscriptions (id, organization_id, plan_id, status, billing_cycle, current_period_start, current_period_end, payment_method, last4) VALUES
(1, 1, 3, 'ACTIVE', 'MONTHLY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'CARD', '4242'),
(2, 2, 2, 'ACTIVE', 'YEARLY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '365' DAY, 'CARD', '8888');

-- Seed Departments
INSERT INTO departments (id, organization_id, name, description) VALUES
(1, 1, 'Engineering', 'Software development and technical infrastructure'),
(2, 1, 'Human Resources', 'Talent acquisition, employee benefits, and workplace culture'),
(3, 1, 'Operations & Finance', 'Financial operations, budgets, and business procurement'),
(4, 2, 'Product Design', 'UI/UX and product experience research'),
(5, 2, 'Customer Success', 'Client satisfaction, onboarding, and escalations');

-- Seed Users (BCrypt for Password123! is $2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q or configured via DataInitializer)
-- Roles: SUPER_ADMIN, COMPANY_ADMIN, MANAGER, EMPLOYEE
INSERT INTO users (id, organization_id, department_id, manager_id, email, password_hash, first_name, last_name, title, phone, role, status) VALUES
(1, 1, NULL, NULL, 'superadmin@flowpilot.ai', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'Alexander', 'Vance', 'Platform Super Admin', '+1-555-0100', 'SUPER_ADMIN', 'ACTIVE'),
(2, 1, 1, NULL, 'admin@acme.com', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'Eleanor', 'Pemberton', 'VP of Engineering / Admin', '+1-555-0101', 'COMPANY_ADMIN', 'ACTIVE'),
(3, 1, 1, 2, 'manager@acme.com', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'Marcus', 'Sterling', 'Engineering Manager', '+1-555-0102', 'MANAGER', 'ACTIVE'),
(4, 1, 1, 3, 'employee@acme.com', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'David', 'Chen', 'Senior Software Engineer', '+1-555-0103', 'EMPLOYEE', 'ACTIVE'),
(5, 1, 2, 2, 'sarah.hr@acme.com', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'Sarah', 'Jenkins', 'HR Operations Director', '+1-555-0104', 'MANAGER', 'ACTIVE'),
(6, 2, 4, NULL, 'admin@techflow.com', '$2a$10$w81yP90fJ1sU24YlK9oNiu4V8nJkO33y9W9kL6M1y9l1m3n2o4p5q', 'Sophia', 'Rodriguez', 'Founder & CEO', '+1-555-0201', 'COMPANY_ADMIN', 'ACTIVE');

-- Seed Workflows
INSERT INTO workflows (id, organization_id, created_by_user_id, name, description, category, status, version, definition_json) VALUES
(1, 1, 3, 'Employee Leave Request Workflow', 'Automated sequential review for personal and vacation leaves requiring manager approval', 'HUMAN_RESOURCES', 'ACTIVE', 1, 
'{"nodes":[{"id":"start_1","type":"START","title":"Leave Application Submitted","x":250,"y":50},{"id":"appr_1","type":"APPROVAL","title":"Manager Decision","x":250,"y":160},{"id":"cond_1","type":"CONDITION","title":"Is Approved?","x":250,"y":270},{"id":"task_1","type":"TASK","title":"Update HR Leave Records","x":120,"y":380},{"id":"notif_1","type":"NOTIFICATION","title":"Notify Employee Rejection","x":380,"y":380},{"id":"end_1","type":"END","title":"Process Concluded","x":250,"y":490}],"edges":[{"source":"start_1","target":"appr_1"},{"source":"appr_1","target":"cond_1"},{"source":"cond_1","target":"task_1","condition":"APPROVED"},{"source":"cond_1","target":"notif_1","condition":"REJECTED"},{"source":"task_1","target":"end_1"},{"source":"notif_1","target":"end_1"}]}'),

(2, 1, 2, 'Corporate Expense Reimbursement', 'Multi-tier verification for travel and operational receipts exceeding $500', 'FINANCE', 'ACTIVE', 1,
'{"nodes":[{"id":"start_2","type":"START","title":"Expense Filed","x":250,"y":50},{"id":"appr_2","type":"APPROVAL","title":"Finance Review","x":250,"y":170},{"id":"end_2","type":"END","title":"Disbursement Queued","x":250,"y":290}],"edges":[{"source":"start_2","target":"appr_2"},{"source":"appr_2","target":"end_2"}]}');

-- Seed Tasks
INSERT INTO tasks (id, organization_id, workflow_execution_id, title, description, assigned_to_user_id, created_by_user_id, priority, status, due_date) VALUES
(1, 1, NULL, 'Audit Q3 Cloud Infrastructure Costs', 'Review AWS and GCP reserved instances across production clusters', 4, 3, 'HIGH', 'IN_PROGRESS', CURRENT_TIMESTAMP + INTERVAL '3' DAY),
(2, 1, NULL, 'Prepare Annual SOC2 Type II Evidence', 'Gather access review logs and multi-tenant isolation verification reports', 4, 2, 'URGENT', 'TODO', CURRENT_TIMESTAMP + INTERVAL '7' DAY),
(3, 1, NULL, 'Configure Tenant Webhooks for ERP Integration', 'Set up outbound payload delivery and retry exponential backoff', 3, 2, 'MEDIUM', 'DONE', CURRENT_TIMESTAMP - INTERVAL '1' DAY),
(4, 1, NULL, 'Update Onboarding Welcome Checklist', 'Sync revised benefits PDF and workspace invitation flow', 5, 2, 'LOW', 'DONE', CURRENT_TIMESTAMP - INTERVAL '2' DAY);

-- Seed Task Comments
INSERT INTO task_comments (id, task_id, user_id, comment_text) VALUES
(1, 1, 4, 'Completed audit for Kubernetes worker nodes; 12% savings identified in compute fleet.'),
(2, 1, 3, 'Great progress David! Please ensure the memory-optimized instances are factored in.');

-- Seed Approvals (Critical Scenario Initial State)
INSERT INTO approvals (id, organization_id, workflow_execution_id, requester_user_id, approver_user_id, request_type, title, details_json, status, decision_notes) VALUES
(1, 1, NULL, 4, 3, 'LEAVE', 'Annual Leave Request (3 Days)', '{"startDate":"2026-11-15","endDate":"2026-11-18","reason":"Attending regional systems architecture summit","coverageAssigned":"Sarah Jenkins"}', 'PENDING', NULL),
(2, 1, NULL, 4, 3, 'EXPENSE', 'Conference Registration & Travel Receipt', '{"amount":645.00,"currency":"USD","category":"Professional Development","vendor":"DevOps Global"}', 'PENDING', NULL),
(3, 1, NULL, 5, 2, 'PURCHASE', 'Ergonomic Workspace Hardware Upgrade', '{"amount":320.00,"currency":"USD","item":"Dual Monitor Arm and Standing Desk Converter"}', 'APPROVED', 'Approved as per annual home office wellness allowance.');

-- Seed Notifications
INSERT INTO notifications (id, organization_id, user_id, title, message, type, link_url, is_read) VALUES
(1, 1, 3, 'New Leave Approval Requested', 'David Chen has submitted an Annual Leave Request for 3 days awaiting your approval.', 'APPROVAL_REQUEST', '/approvals', FALSE),
(2, 1, 4, 'Task Assigned: Infrastructure Audit', 'Marcus Sterling assigned you to "Audit Q3 Cloud Infrastructure Costs".', 'TASK_ASSIGNED', '/tasks', FALSE),
(3, 1, 5, 'Hardware Purchase Approved', 'Your purchase request for Ergonomic Workspace Hardware was approved by Eleanor.', 'REQUEST_APPROVED', '/approvals', TRUE);

-- Seed Audit Logs
INSERT INTO audit_logs (id, organization_id, user_id, action, entity_type, entity_id, ip_address, details_json) VALUES
(1, 1, 2, 'CREATE_WORKFLOW', 'WORKFLOW', '1', '192.168.1.10', '{"name":"Employee Leave Request Workflow"}'),
(2, 1, 4, 'SUBMIT_REQUEST', 'APPROVAL', '1', '192.168.1.42', '{"type":"LEAVE","title":"Annual Leave Request (3 Days)"}');
