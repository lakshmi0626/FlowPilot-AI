# Changelog

All notable changes to the **FlowPilot AI** enterprise workflow automation platform will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-11-05

### Added
- **Authentication & Multi-Tenant Security:**
  - Multi-tenant JWT generation with encrypted claims (`sub`, `orgId`, `role`, `email`).
  - BCrypt password encoder with configurable work factor 12.
  - Role-Based Access Control supporting `SUPER_ADMIN`, `COMPANY_ADMIN`, `MANAGER`, and `EMPLOYEE`.
  - Stateless request filtering with ThreadLocal `TenantContext` isolation.
  - Atomic organization registration endpoint seeding initial departments and admin accounts.
  - Cryptographic employee invitation token dispatch and acceptance workflows.
  - Standardized RFC 7807 problem details in `GlobalExceptionHandler` and `ErrorResponse`.

- **Visual Workflow Builder & Execution Engine:**
  - Interactive React Flow canvas with custom nodes: Start, Task, Approval, Condition, Notification, and End.
  - Emerald green graph visual styling with zero blue colors.
  - Drag-and-drop node template palette with graph validation heuristics.
  - Sequential and conditional execution engine with variable expression parsing.
  - Human-in-the-loop approval pause (`WAITING_APPROVAL`) and automatic downstream node resumption.
  - Step-level audit logging in `WorkflowExecutionLog` with duration calculation.

- **Task Management & Collaborations:**
  - Task CRUD endpoints with priority (`LOW`, `MEDIUM`, `HIGH`, `URGENT`) and status (`TODO`, `IN_PROGRESS`, `DONE`).
  - Threaded task commenting system with author tagging and timestamps.
  - Automated overdue task detection and manager escalation alerts.
  - Responsive `TasksPage` with dynamic search, filter pills, and assignment modals.

- **Approval Center:**
  - Support for Purchase, Leave, Expense, and Custom organizational requests.
  - Decision capture endpoint with mandatory justification notes and status transitions.
  - Workflow resumption hooks upon approval decision.
  - Client-side CSV audit export in `ApprovalsPage`.

- **Notifications & Telemetry:**
  - In-app notification center with TopNav badge counter and dropdown popover.
  - Individual and bulk `markAsRead` endpoints.
  - Toast banner notification system for ephemeral action alerts.
  - Executive dashboard statistics aggregation (`DashboardStatsDTO`).
  - Interactive Recharts AreaChart and BarChart for monthly volume and department velocity.

- **AI Workflow Synthesis Engine:**
  - Natural language workflow synthesis generating React Flow nodes and edges.
  - Enterprise prompt blueprint library (Onboarding, Procurement, Incident Response, Expense Approval).
  - Interactive `AiAssistantPage` with direct canvas handoff to `WorkflowBuilderPage`.

- **DevOps, Testing & Quality Assurance:**
  - Multi-stage Docker packaging for Spring Boot backend and React Nginx frontend.
  - `docker-compose.yml` orchestrating PostgreSQL, Backend, and Frontend.
  - Dual-job GitHub Actions CI workflow for JDK 17 Maven and Node 20 Vite.
  - Comprehensive unit test suites for `AuthServiceTest`, `WorkflowEngineServiceTest`, `TaskServiceTest`, and `ApprovalServiceTest`.
  - Spring Boot application context boot verification in `FlowPilotApplicationTests`.
  - Frontend Vitest unit test suite in `App.test.jsx`.

### Changed
- Refined Maven Surefire test runner with `mock-maker-subclass` and in-process execution for JDK 21+ / JDK 26 compatibility.
- Updated `ci.yml` with dependency caching and full test packaging.
- Enhanced `WorkflowEngineService.resumeWorkflow()` with recursive step traversal across subsequent graph nodes.

### Fixed
- Fixed ByteBuddy dynamic agent loading warnings on modern JDK versions.
- Resolved JPA open-in-view warnings by disabling view query execution in dev profile.
- Fixed Tomcat test socket lingering by specifying `webEnvironment = NONE` in Spring Boot tests.
