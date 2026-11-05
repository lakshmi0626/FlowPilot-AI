# FlowPilot AI – Comprehensive Commercial Engineering PR Backlog
### 100+ Genuinely Distinct Enterprise Pull Request Tasks

This document defines the 100+ distinct engineering tasks for FlowPilot AI across architectural domains: Authentication & RBAC, Multi-Tenancy & Security, Workflow Modeling & React Flow Builder, Backend Execution Engine, Task Management, Human-in-the-Loop Approvals, Notifications & Webhooks, Real-Time Analytics & Reporting, AI Workflow Synthesis, Billing & Subscriptions, and DevOps/Quality Assurance.

---

## Domain 1: Authentication, Authorization & Identity Management (PRs #001 – #012)

### PR #001: Implement Multi-Tenant JWT Token Generation and Custom Claims
- **Feature Area**: Authentication & Security
- **Objective**: Embed tenant ID, user role, and session expiration securely into signed JWT tokens.
- **Implementation Scope**: `JwtTokenProvider.java`, `SecurityConfig.java`, user claim enrichment.
- **Acceptance Criteria**: Tokens contain `sub`, `orgId`, `role`, and `email`; HS256/RS256 validation enforced.
- **Suggested Branch**: `feature/authentication-jwt-claims`
- **Testing Requirements**: Unit test validating token claim extraction and tampering detection.

### PR #002: BCrypt Password Hashing with Configurable Strength and Salt Validation
- **Feature Area**: Security
- **Objective**: Enforce modern cryptographic hashing on user credentials during registration and updates.
- **Implementation Scope**: `PasswordEncoder` bean configuration in `SecurityConfig.java`.
- **Acceptance Criteria**: Passwords hashed with BCrypt strength 12; raw passwords never logged or persisted.
- **Suggested Branch**: `feature/security-password-hashing`
- **Testing Requirements**: Password encoding test verifying match against salt variants.

### PR #003: Stateless JWT Authentication Filter with ThreadLocal Tenant Isolation
- **Feature Area**: Security & Multi-Tenancy
- **Objective**: Intercept every incoming HTTP request, parse JWT, and bind tenant context to `TenantContext`.
- **Implementation Scope**: `JwtAuthenticationFilter.java`, `TenantContext.java`.
- **Acceptance Criteria**: Requests without valid Bearer token rejected with 401; `TenantContext` populated and cleared after request filter chain.
- **Suggested Branch**: `feature/tenant-context-filter`
- **Testing Requirements**: Filter chain mock test verifying `TenantContext.getCurrentTenant()` during execution.

### PR #004: Role-Based Endpoint Authorization & PreAuthorize Annotations
- **Feature Area**: Authorization
- **Objective**: Restrict administrative endpoints to `COMPANY_ADMIN` and `SUPER_ADMIN`.
- **Implementation Scope**: Add `@PreAuthorize("hasRole('COMPANY_ADMIN')")` across administrative controllers.
- **Acceptance Criteria**: Employees attempting admin endpoints receive HTTP 403 Forbidden.
- **Suggested Branch**: `feature/rbac-endpoint-authorization`
- **Testing Requirements**: Controller security tests with varied MockUser authorities.

### PR #005: Corporate Organization Registration Endpoint with Seeded Default Departments
- **Feature Area**: Organization Onboarding
- **Objective**: Enable new organizations to register in a single atomic transaction.
- **Implementation Scope**: `AuthService.registerCompany()`, `AuthController.java`.
- **Acceptance Criteria**: Creates Organization, Admin User, Engineering/HR/Finance departments, and Free Trial subscription atomically.
- **Suggested Branch**: `feature/organization-registration`
- **Testing Requirements**: Integration test verifying atomic rollback upon email duplication.

### PR #006: Secure Password Reset & Credential Rotation Workflow
- **Feature Area**: Authentication
- **Objective**: Allow users to reset passwords with verifiable identity tokens.
- **Implementation Scope**: `AuthService.resetPassword()`, `PasswordResetRequest.java`.
- **Acceptance Criteria**: Updates password hash; records audit entry; invalidates existing sessions.
- **Suggested Branch**: `feature/auth-password-reset`
- **Testing Requirements**: Service test verifying new hash match and rejection of invalid emails.

### PR #007: Frontend Auth Context & Token Synchronization
- **Feature Area**: Frontend Auth
- **Objective**: Provide React context storing user identity, JWT, and tenant status.
- **Implementation Scope**: `AuthContext.jsx`, `api.js` Axios request interceptor.
- **Acceptance Criteria**: Token automatically attached in `Authorization` header on all outgoing Axios requests.
- **Suggested Branch**: `feature/frontend-auth-context`
- **Testing Requirements**: React testing library test for provider token persistence in localStorage.

### PR #008: Role Switcher Persona Bar for Evaluator & QA Testing
- **Feature Area**: Developer Experience & Testing
- **Objective**: Enable instant 1-click persona switching (Admin, Manager, Employee) for end-to-end scenario validation.
- **Implementation Scope**: `TopNav.jsx`, `AuthContext.jsx`.
- **Acceptance Criteria**: Dropdown updates active JWT context and refreshes page permissions instantly.
- **Suggested Branch**: `feature/persona-testing-switcher`
- **Testing Requirements**: Interaction test verifying role change updates sidebar items.

### PR #009: Session Expiration & Refresh Token Interceptor
- **Feature Area**: Security
- **Objective**: Gracefully handle 401 token expiration and prompt user without data loss.
- **Implementation Scope**: `api.js` response interceptor.
- **Acceptance Criteria**: 401 responses redirect cleanly to `/login` with return URL state.
- **Suggested Branch**: `feature/session-expiration-handling`
- **Testing Requirements**: Mock HTTP 401 response test verifying redirect.

### PR #010: Email Sanitization & Case-Insensitive Uniqueness Verification
- **Feature Area**: Data Integrity
- **Objective**: Prevent duplicate accounts through whitespace or mixed-case email inputs.
- **Implementation Scope**: `UserRepository.java`, `AuthService.java`.
- **Acceptance Criteria**: Emails trimmed and stored lowercase (`user@company.com`).
- **Suggested Branch**: `feature/email-sanitization`
- **Testing Requirements**: Service test verifying `User@Company.com` matches `user@company.com`.

### PR #011: Audit Logging for Authentication Events (Login, Logout, Reset)
- **Feature Area**: Compliance & Audit
- **Objective**: Record all authentication actions with timestamp, user ID, and IP address.
- **Implementation Scope**: `AuditLogService.java`, `audit_logs` table.
- **Acceptance Criteria**: Login and password changes produce tamper-evident audit records.
- **Suggested Branch**: `feature/auth-audit-logging`
- **Testing Requirements**: Repository test verifying audit log insertion upon login.

### PR #012: User Profile API with Department and Direct Manager Linkage
- **Feature Area**: User Management
- **Objective**: Provide `/api/auth/me` with resolved department names and direct manager details.
- **Implementation Scope**: `AuthController.getCurrentUser()`, `UserDto.java`.
- **Acceptance Criteria**: Returns complete profile object without exposing password hash.
- **Suggested Branch**: `feature/user-profile-endpoint`
- **Testing Requirements**: Controller mock test verifying JSON serialization excludes `passwordHash`.

---

## Domain 2: Multi-Tenancy, Organization & Workspace Settings (PRs #013 – #022)

### PR #013: Organization Workspace URL Slug Validation and Collision Avoidance
- **Feature Area**: Multi-Tenancy
- **Objective**: Auto-generate clean URL slugs from company names with numeric collision handling.
- **Implementation Scope**: `AuthService.registerCompany()`, regex slugifier.
- **Acceptance Criteria**: "Acme Corp!" becomes `acme-corp`; collisions append incremental suffixes.
- **Suggested Branch**: `feature/slug-generator`
- **Testing Requirements**: Unit tests with special characters, emojis, and identical company names.

### PR #014: Tenant Isolation Enforcer in JPA Repository Method Queries
- **Feature Area**: Multi-Tenancy & Database
- **Objective**: Enforce `WHERE organization_id = :orgId` on all workflow, task, and approval queries.
- **Implementation Scope**: `WorkflowRepository`, `TaskRepository`, `ApprovalRepository`.
- **Acceptance Criteria**: Cross-tenant data leakage is mathematically impossible at the query layer.
- **Suggested Branch**: `feature/tenant-query-isolation`
- **Testing Requirements**: Multi-tenant repository tests asserting Tenant A never reads Tenant B data.

### PR #015: Department Taxonomy Management CRUD API
- **Feature Area**: Organization Settings
- **Objective**: Allow company administrators to manage departmental divisions.
- **Implementation Scope**: `OrganizationService.java`, `OrganizationController.java`.
- **Acceptance Criteria**: Create, list, and associate departments per organization.
- **Suggested Branch**: `feature/department-management-api`
- **Testing Requirements**: Unit test asserting department lists are scoped to caller's tenant.

### PR #016: Workspace Branding & Design System Token Customization
- **Feature Area**: Settings & UI
- **Objective**: Persist organization custom display names while enforcing non-blue palette guidelines.
- **Implementation Scope**: `Organization.java`, `SettingsPage.jsx`.
- **Acceptance Criteria**: Organization primary color constrained to approved Emerald/Forest green spectrum.
- **Suggested Branch**: `feature/workspace-branding`
- **Testing Requirements**: Validation test rejecting blue `#0000FF` hex codes.

### PR #017: Multi-Tenant Database Seed Scripts for Acme Corp & TechFlow
- **Feature Area**: Database & Seeding
- **Objective**: Seed two distinct enterprise tenants with complete users, roles, and workflows.
- **Implementation Scope**: `V2__seed_demo_data.sql`, `DataInitializer.java`.
- **Acceptance Criteria**: Seeds Acme Corp (ID: 1) and TechFlow (ID: 2) on startup if database is empty.
- **Suggested Branch**: `feature/database-seed-tenants`
- **Testing Requirements**: Flyway / CommandLineRunner verification test.

### PR #018: Tenant Settings UI Form with Real-Time Validation
- **Feature Area**: Frontend Settings
- **Objective**: Provide settings page allowing company name, domain, and department updates.
- **Implementation Scope**: `SettingsPage.jsx`.
- **Acceptance Criteria**: Form validates inputs, displays loading state, and shows success toast upon save.
- **Suggested Branch**: `feature/settings-ui-form`
- **Testing Requirements**: Component test verifying PUT request dispatched with updated fields.

### PR #019: Employee Directory Table with Department and Role Filters
- **Feature Area**: Employee Management
- **Objective**: Display team directory with live search, department selector, and role pills.
- **Implementation Scope**: `EmployeesPage.jsx`.
- **Acceptance Criteria**: Search filters records by name, email, or title in real time.
- **Suggested Branch**: `feature/employee-directory-ui`
- **Testing Requirements**: Vitest test verifying filtering behavior on 10+ mock employee records.

### PR #020: Employee Creation Modal with Manager Assignment Hierarchy
- **Feature Area**: Employee Management
- **Objective**: Modal allowing admins to add employees and link their reporting manager.
- **Implementation Scope**: `EmployeeCreateRequest.java`, `EmployeesPage.jsx`.
- **Acceptance Criteria**: Saves employee to backend, establishes `manager_id`, and refreshes table.
- **Suggested Branch**: `feature/employee-create-modal`
- **Testing Requirements**: Form submit mock test asserting POST payload structure.

### PR #021: Employee Invitation Token Generation & Expiration Handling
- **Feature Area**: Onboarding
- **Objective**: Generate secure 7-day invitation tokens for invited colleagues.
- **Implementation Scope**: `Invitation.java`, `EmployeeService.inviteEmployee()`.
- **Acceptance Criteria**: Creates UUID token, sets `expires_at = now + 7 days`, status `PENDING`.
- **Suggested Branch**: `feature/invitation-token-generator`
- **Testing Requirements**: Service test verifying expiration timestamp computation.

### PR #022: Employee Account Suspension & Deactivation Logic
- **Feature Area**: Employee Management
- **Objective**: Allow admins to suspend employee access without deleting historical workflow records.
- **Implementation Scope**: `EmployeeService.deactivateEmployee()`.
- **Acceptance Criteria**: User status set to `SUSPENDED`; login attempts rejected with 401/403.
- **Suggested Branch**: `feature/employee-deactivation`
- **Testing Requirements**: Integration test verifying suspended user cannot authenticate.

---

## Domain 3: Workflow Modeling & React Flow Builder (PRs #023 – #035)

### PR #023: React Flow Workflow Builder Canvas Initialization
- **Feature Area**: Workflow Builder
- **Objective**: Embed visual canvas with pan, zoom, grid snapping, and node palette.
- **Implementation Scope**: `WorkflowBuilderPage.jsx`.
- **Acceptance Criteria**: Canvas renders with emerald background grid dots and drag-and-drop support.
- **Suggested Branch**: `feature/builder-canvas-setup`
- **Testing Requirements**: Vitest component render test for builder canvas.

### PR #024: Start Node Component with Trigger Parameter Configuration
- **Feature Area**: Workflow Builder
- **Objective**: Define Start node visually with trigger type (Manual, Scheduled, Webhook).
- **Implementation Scope**: `WorkflowBuilderPage.jsx` node definitions.
- **Acceptance Criteria**: Node styled with deep forest green header; emits outgoing connection ports.
- **Suggested Branch**: `feature/builder-start-node`
- **Testing Requirements**: Verify Start node attributes serialize correctly to JSON.

### PR #025: Automated Task Node Component with Action Configurations
- **Feature Area**: Workflow Builder
- **Objective**: Node representing automated background tasks and system integrations.
- **Implementation Scope**: `WorkflowBuilderPage.jsx`.
- **Acceptance Criteria**: Configurable title, action type, and retry parameters.
- **Suggested Branch**: `feature/builder-task-node`
- **Testing Requirements**: Verify Task node payload generation.

### PR #026: Human Approval Gate Node with Assignee Role Configuration
- **Feature Area**: Workflow Builder
- **Objective**: Node representing human-in-the-loop review (Manager, Finance, HR).
- **Implementation Scope**: Node component styled with Warm Amber `#E5A43B`.
- **Acceptance Criteria**: Configurable approver role, timeout days, and escalation fallback.
- **Suggested Branch**: `feature/builder-approval-node`
- **Testing Requirements**: Assert node serializes `approverRole` and `requestType`.

### PR #027: Conditional Rule Node with Branching Logic
- **Feature Area**: Workflow Builder
- **Objective**: Branch execution based on expressions (e.g. `amount > 500` or `decision == APPROVED`).
- **Implementation Scope**: Diamond / Condition pill node with labeled output ports.
- **Acceptance Criteria**: Supports `APPROVED` and `REJECTED` directional edges.
- **Suggested Branch**: `feature/builder-condition-node`
- **Testing Requirements**: Test conditional edge serialization.

### PR #028: User Notification Node Component with Multi-Channel Templates
- **Feature Area**: Workflow Builder
- **Objective**: Node dispatching alerts to employees or managers upon step milestones.
- **Implementation Scope**: Notification node styled with Emerald `#269B64`.
- **Acceptance Criteria**: Configurable message template and target recipient variable.
- **Suggested Branch**: `feature/builder-notification-node`
- **Testing Requirements**: Assert template string validation.

### PR #029: Terminal End Node Component with Output Summaries
- **Feature Area**: Workflow Builder
- **Objective**: Concludes workflow execution and marks final state (Completed / Terminated).
- **Implementation Scope**: End node styled with Coral Red `#D9534F`.
- **Acceptance Criteria**: Enforces that outgoing edges cannot be attached to End nodes.
- **Suggested Branch**: `feature/builder-end-node`
- **Testing Requirements**: Node connection validation test.

### PR #030: Interactive Edge Connector with SVG Bezier Curves and Directional Arrows
- **Feature Area**: Workflow Builder
- **Objective**: Render smooth curved Bezier connection lines with arrowheads between nodes.
- **Implementation Scope**: SVG path calculations in `WorkflowBuilderPage.jsx`.
- **Acceptance Criteria**: Arrows point from source output to target input port in emerald color.
- **Suggested Branch**: `feature/builder-edge-connector`
- **Testing Requirements**: SVG path calculation unit test.

### PR #031: Node Property Inspector Drawer
- **Feature Area**: Workflow Builder
- **Objective**: Side panel displaying selected node properties with live text inputs.
- **Implementation Scope**: Node inspector in `WorkflowBuilderPage.jsx`.
- **Acceptance Criteria**: Modifying node label in inspector updates canvas node immediately.
- **Suggested Branch**: `feature/builder-property-inspector`
- **Testing Requirements**: Component test verifying two-way property binding.

### PR #032: Graph Topology Validator (No Disconnected Nodes / Missing Terminals)
- **Feature Area**: Workflow Builder
- **Objective**: Inspect graph before save; assert presence of Start, End, and connectivity.
- **Implementation Scope**: `validateWorkflow()` algorithm in builder.
- **Acceptance Criteria**: Flags missing Start node, isolated orphan nodes, or cycle deadlocks.
- **Suggested Branch**: `feature/builder-graph-validator`
- **Testing Requirements**: Unit test suite with invalid graphs (missing start, missing end, isolated nodes).

### PR #033: Workflow Definition Persistence API (JSON Schema Storage)
- **Feature Area**: Workflow Backend
- **Objective**: Persist nodes, edges, and configurations into `workflows.definition_json`.
- **Implementation Scope**: `WorkflowService.java`, `WorkflowController.java`.
- **Acceptance Criteria**: Saves definition, increments version number, updates `updated_at`.
- **Suggested Branch**: `feature/workflow-persistence-api`
- **Testing Requirements**: Repository integration test verifying JSON string retrieval.

### PR #034: Workflow Duplication Feature with Suffix Appender
- **Feature Area**: Workflow Management
- **Objective**: Clone an existing workflow definition into a new draft workflow.
- **Implementation Scope**: `WorkflowService.duplicateWorkflow()`.
- **Acceptance Criteria**: Creates new workflow with name `[Original Name] (Copy)` and status `DRAFT`.
- **Suggested Branch**: `feature/workflow-duplication`
- **Testing Requirements**: Service test verifying exact copy of nodes and edges under new ID.

### PR #035: Workflow Status Toggle (Active / Paused) with Instant Propagation
- **Feature Area**: Workflow Management
- **Objective**: Allow users to pause workflows to prevent new triggers during maintenance.
- **Implementation Scope**: `PATCH /api/workflows/{id}/toggle-status`.
- **Acceptance Criteria**: Updates status between `ACTIVE` and `PAUSED`; returns updated record.
- **Suggested Branch**: `feature/workflow-status-toggle`
- **Testing Requirements**: Controller test verifying status toggling.

---

## Domain 4: Backend Workflow Execution Engine (PRs #036 – #048)

### PR #036: Execution Instance Model & Lifecycle State Machine
- **Feature Area**: Workflow Engine
- **Objective**: Establish `WorkflowExecution` entity with status `RUNNING`, `PAUSED_APPROVAL`, `COMPLETED`, `REJECTED`, `FAILED`.
- **Implementation Scope**: `WorkflowExecution.java`, `ExecutionStatus.java`.
- **Acceptance Criteria**: Tracks `started_at`, `completed_at`, `current_node_key`, and `payload_json`.
- **Suggested Branch**: `feature/execution-lifecycle-model`
- **Testing Requirements**: Entity mapping and enum serialization test.

### PR #037: Workflow Execution Engine Core Dispatcher
- **Feature Area**: Workflow Engine
- **Objective**: Core engine method `executeWorkflow(orgId, workflowId, userId, payload)`.
- **Implementation Scope**: `WorkflowEngineService.java`.
- **Acceptance Criteria**: Instantiates execution record, creates initial log, starts processing at Start node.
- **Suggested Branch**: `feature/engine-core-dispatcher`
- **Testing Requirements**: Integration test verifying execution record created in database.

### PR #038: Sequential Node Traversal & Edge Follower Logic
- **Feature Area**: Workflow Engine
- **Objective**: Engine navigates from current node to next node by matching edge `source` and `target`.
- **Implementation Scope**: `findNextNode()` in `WorkflowEngineService.java`.
- **Acceptance Criteria**: Follows linear sequence `Start -> Task -> End` and records logs for each step.
- **Suggested Branch**: `feature/engine-sequential-traversal`
- **Testing Requirements**: Traversal test verifying 3-step linear flow execution.

### PR #039: Human Approval Pause Mechanism in Execution Engine
- **Feature Area**: Workflow Engine & Critical Scenario
- **Objective**: When engine encounters `APPROVAL` node, transition execution to `PAUSED_APPROVAL`.
- **Implementation Scope**: `WorkflowEngineService.java`, `Approval.java`.
- **Acceptance Criteria**: Execution state halts at approval node; creates real `Approval` record; does NOT advance until approved.
- **Suggested Branch**: `feature/engine-approval-pause`
- **Testing Requirements**: Test asserting execution remains in `PAUSED_APPROVAL` and creates approval entity.

### PR #040: Workflow Resumption After Manager Decision
- **Feature Area**: Workflow Engine & Critical Scenario
- **Objective**: Resume execution when manager approves or rejects request in Approval Center.
- **Implementation Scope**: `WorkflowEngineService.resumeWorkflow()`.
- **Acceptance Criteria**: If approved, engine advances to subsequent node; if rejected, marks execution `REJECTED`.
- **Suggested Branch**: `feature/engine-workflow-resumption`
- **Testing Requirements**: Integration test verifying transition from `PAUSED_APPROVAL` to `COMPLETED`.

### PR #041: Conditional Branch Evaluation (APPROVED vs REJECTED Edge Routing)
- **Feature Area**: Workflow Engine
- **Objective**: Follow edge with condition matching decision (`condition == 'APPROVED'`).
- **Implementation Scope**: Conditional routing logic in `WorkflowEngineService.java`.
- **Acceptance Criteria**: Approved route executes HR update; rejected route routes to employee feedback notification.
- **Suggested Branch**: `feature/engine-conditional-routing`
- **Testing Requirements**: Unit test asserting correct edge followed based on condition string.

### PR #042: Execution Step Audit Logger (`workflow_execution_logs`)
- **Feature Area**: Workflow Engine & Observability
- **Objective**: Persist timestamped record of every node evaluation with status and details.
- **Implementation Scope**: `WorkflowExecutionLog.java`, `WorkflowExecutionLogRepository.java`.
- **Acceptance Criteria**: Records `node_key`, `step_name`, `status`, `message`, and timestamp for every step.
- **Suggested Branch**: `feature/engine-step-audit-logger`
- **Testing Requirements**: Verify step logs populated during execution.

### PR #043: Automated Task Generator for Workflow Steps
- **Feature Area**: Workflow Engine & Tasks Integration
- **Objective**: Automatically create a `Task` entity when engine processes a `TASK` node.
- **Implementation Scope**: `WorkflowEngineService.processWorkflow()`.
- **Acceptance Criteria**: Creates Task assigned to user with title derived from node configuration.
- **Suggested Branch**: `feature/engine-task-generator`
- **Testing Requirements**: Verify `tasks` table has new record linked to `workflow_execution_id`.

### PR #044: Real-Time Execution Step Stream API
- **Feature Area**: Workflow Engine API
- **Objective**: Expose `/api/executions/{id}/logs` for frontend live execution visualizer.
- **Implementation Scope**: `WorkflowExecutionController.java`.
- **Acceptance Criteria**: Returns array of ordered log records for the given execution instance.
- **Suggested Branch**: `feature/execution-logs-api`
- **Testing Requirements**: Controller mock test asserting log array JSON response.

### PR #045: Execution Failure Handling & Error Message Capture
- **Feature Area**: Engine Resilience
- **Objective**: Catch runtime exceptions gracefully, update status to `FAILED`, record error message.
- **Implementation Scope**: Try-catch exception handling in `WorkflowEngineService.java`.
- **Acceptance Criteria**: Malformed JSON or missing target nodes sets status `FAILED` without crashing server.
- **Suggested Branch**: `feature/engine-error-handling`
- **Testing Requirements**: Malformed definition test asserting `FAILED` status and error message populated.

### PR #046: Live Execution Modal in Workflow Manager
- **Feature Area**: Frontend Workflows
- **Objective**: Modal allowing users to trigger workflows with custom payload and view live status.
- **Implementation Scope**: `WorkflowsPage.jsx` Run Modal.
- **Acceptance Criteria**: Dispatches run request, displays execution outcome badge, links to Approval Center if paused.
- **Suggested Branch**: `feature/workflows-run-modal-ui`
- **Testing Requirements**: Component test verifying modal opens, inputs payload, and displays result.

### PR #047: Workflow Execution History Drawer
- **Feature Area**: Workflow Observability
- **Objective**: View past execution runs of a workflow with durations and outcomes.
- **Implementation Scope**: `WorkflowExecutionRepository.findByWorkflowId()`.
- **Acceptance Criteria**: Lists historical runs with badges (COMPLETED, PAUSED, FAILED).
- **Suggested Branch**: `feature/execution-history-drawer`
- **Testing Requirements**: Query test verifying runs filtered by `workflow_id`.

### PR #048: Automated Completion Notification Dispatcher
- **Feature Area**: Workflow Engine & Notifications
- **Objective**: Automatically notify the workflow initiator when execution finishes.
- **Implementation Scope**: `NotificationService.sendNotification()`.
- **Acceptance Criteria**: Sends `WORKFLOW_COMPLETED` notification with link to `/workflows`.
- **Suggested Branch**: `feature/engine-completion-notification`
- **Testing Requirements**: Mock verify notification dispatched on completion.

---

## Domain 5: Task Management & Collaboration (PRs #049 – #060)

### PR #049: Task Entity with Priority, Status, and Due Date Fields
- **Feature Area**: Tasks Model
- **Objective**: Full relational entity for team tasks with priority and status enums.
- **Implementation Scope**: `Task.java`, `TaskPriority.java`, `TaskStatus.java`.
- **Acceptance Criteria**: Enforces priorities `LOW, MEDIUM, HIGH, URGENT` and statuses `TODO, IN_PROGRESS, REVIEW, DONE`.
- **Suggested Branch**: `feature/task-entity-model`
- **Testing Requirements**: JPA entity persistence test.

### PR #050: Task Filtering & Multi-Column Sorting API
- **Feature Area**: Tasks API
- **Objective**: GET `/api/tasks` supporting query parameters `status`, `priority`, and `assigneeId`.
- **Implementation Scope**: `TaskService.getTasks()`, `TaskController.java`.
- **Acceptance Criteria**: Returns tenant-isolated list matching active query filters.
- **Suggested Branch**: `feature/task-filtering-api`
- **Testing Requirements**: Service test verifying status and priority filter combinations.

### PR #051: Task Creation Form Modal with Assignee Dropdown
- **Feature Area**: Tasks UI
- **Objective**: Modal allowing users to create tasks with title, description, priority, assignee, and due date.
- **Implementation Scope**: `TasksPage.jsx`, `TaskCreateRequest.java`.
- **Acceptance Criteria**: Form validates required fields, submits to `POST /api/tasks`, and prepends to list.
- **Suggested Branch**: `feature/task-create-modal`
- **Testing Requirements**: Interaction test verifying task creation updates Kanban column.

### PR #052: Interactive Kanban Board with Column Counters
- **Feature Area**: Tasks UI
- **Objective**: 4-column drag/button workflow board (TODO, IN_PROGRESS, REVIEW, DONE).
- **Implementation Scope**: `TasksPage.jsx` Kanban layout.
- **Acceptance Criteria**: Each column header shows live count; cards display priority badge, due date, and title.
- **Suggested Branch**: `feature/tasks-kanban-board`
- **Testing Requirements**: Component test asserting correct task grouping into columns.

### PR #053: One-Click Task Status Lane Transition Handlers
- **Feature Area**: Tasks UI
- **Objective**: Provide arrow buttons on cards to transition tasks between lanes instantly.
- **Implementation Scope**: `handleUpdateStatus()` in `TasksPage.jsx`.
- **Acceptance Criteria**: Clicking `→` updates status in backend and moves card to next column.
- **Suggested Branch**: `feature/task-lane-transitions`
- **Testing Requirements**: Click handler test verifying API called with new status.

### PR #054: Dual View Toggle (Kanban Board vs Structured Table)
- **Feature Area**: Tasks UI
- **Objective**: Allow users to switch between Kanban board and detailed tabular view.
- **Implementation Scope**: View toggle buttons in `TasksPage.jsx`.
- **Acceptance Criteria**: Seamless toggle without reloading page or losing filter criteria.
- **Suggested Branch**: `feature/task-view-toggle`
- **Testing Requirements**: Interaction test asserting view switch renders table vs board.

### PR #055: Task Deletion Confirmation Dialog with Backend Deletion
- **Feature Area**: Tasks Management
- **Objective**: Safe deletion modal preventing accidental task removal.
- **Implementation Scope**: Modal dialog in `TasksPage.jsx`, `DELETE /api/tasks/{id}`.
- **Acceptance Criteria**: Deletes record from database, logs audit entry, removes card from UI.
- **Suggested Branch**: `feature/task-deletion-modal`
- **Testing Requirements**: Test asserting deletion removed record from list.

### PR #056: Task Comments Feed Entity & Repository
- **Feature Area**: Collaboration Model
- **Objective**: `TaskComment` model linking user remarks to tasks with timestamps.
- **Implementation Scope**: `TaskComment.java`, `TaskCommentRepository.java`.
- **Acceptance Criteria**: Cascades delete when parent task is removed; orders comments chronologically.
- **Suggested Branch**: `feature/task-comments-entity`
- **Testing Requirements**: Entity relationship test.

### PR #057: Task Comments Drawer with Real-Time Posting
- **Feature Area**: Collaboration UI
- **Objective**: Modal/drawer showing task comments stream and input form for new remarks.
- **Implementation Scope**: `openComments()` in `TasksPage.jsx`.
- **Acceptance Criteria**: Fetches existing comments; posting adds new comment and clears input.
- **Suggested Branch**: `feature/task-comments-drawer`
- **Testing Requirements**: Interaction test verifying new comment appended to comment list.

### PR #058: Task Assignment Notification Trigger
- **Feature Area**: Tasks & Notifications
- **Objective**: Alert an employee when assigned or reassigned to a task.
- **Implementation Scope**: `TaskService.createTask()` and `updateTask()`.
- **Acceptance Criteria**: Dispatches `TASK_ASSIGNED` notification to assignee if not the creator.
- **Suggested Branch**: `feature/task-assignment-notification`
- **Testing Requirements**: Verify notification created for assignee.

### PR #059: Overdue Task Detection & Computation
- **Feature Area**: Tasks & Analytics
- **Objective**: Query and count tasks where `status != 'DONE'` and `dueDate < now`.
- **Implementation Scope**: `TaskRepository.countByOrganizationIdAndStatusNotAndDueDateBefore()`.
- **Acceptance Criteria**: Accurately flags overdue tasks on dashboard metric card.
- **Suggested Branch**: `feature/overdue-task-detector`
- **Testing Requirements**: Query test with past and future dates.

### PR #060: Universal CSV Task Exporter
- **Feature Area**: Reporting & Data Export
- **Objective**: Allow users to download full task register as a standard CSV spreadsheet file.
- **Implementation Scope**: `CsvExportButton.jsx` on `TasksPage.jsx`.
- **Acceptance Criteria**: Generates and downloads `team-tasks.csv` with ID, Title, Priority, Status, Due Date.
- **Suggested Branch**: `feature/tasks-csv-export`
- **Testing Requirements**: Browser download trigger test.

---

## Domain 6: Approval Center & Human-in-the-Loop Decisions (PRs #061 – #072)

### PR #061: Approval Entity with Request Types & Status State Machine
- **Feature Area**: Approvals Model
- **Objective**: Model requests for LEAVE, EXPENSE, PURCHASE, and CUSTOM approvals.
- **Implementation Scope**: `Approval.java`, `ApprovalType.java`, `ApprovalStatus.java`.
- **Acceptance Criteria**: Tracks `requester_user_id`, `approver_user_id`, `workflow_execution_id`, `details_json`, `decision_notes`.
- **Suggested Branch**: `feature/approval-entity-model`
- **Testing Requirements**: Model persistence and status enum validation test.

### PR #062: Approval Center Status Tabs (Pending Review, Approved History, Rejected)
- **Feature Area**: Approvals UI
- **Objective**: Tabbed navigation filtering approval requests by operational state.
- **Implementation Scope**: `ApprovalsPage.jsx`.
- **Acceptance Criteria**: Switching tabs updates request list without page reload.
- **Suggested Branch**: `feature/approval-tabs-ui`
- **Testing Requirements**: Tab click interaction test.

### PR #063: New Approval Request Submission Modal
- **Feature Area**: Approvals UI & Employee Flow
- **Objective**: Form for employees to file Leave, Expense, or Purchase requests.
- **Implementation Scope**: `ApprovalsPage.jsx`, `ApprovalCreateRequest.java`.
- **Acceptance Criteria**: Validates fields, creates `Approval` in `PENDING` state, notifies manager.
- **Suggested Branch**: `feature/approval-submission-modal`
- **Testing Requirements**: Form submit mock test asserting POST payload.

### PR #064: Dynamic Details JSON Parser & Data Grid Card Viewer
- **Feature Area**: Approvals UI
- **Objective**: Parse arbitrary JSON payload (dates, amounts, vendor, reasons) into formatted key-value grid.
- **Implementation Scope**: `parseDetails()` in `ApprovalsPage.jsx`.
- **Acceptance Criteria**: Displays dates, currency, vendor names cleanly in card body.
- **Suggested Branch**: `feature/approval-card-details-parser`
- **Testing Requirements**: Test parsing varied JSON schemas.

### PR #065: Manager Approval Sign-Off Modal with Justification Notes
- **Feature Area**: Approvals Decision
- **Objective**: Modal prompting manager for approval comments before committing decision.
- **Implementation Scope**: Decision modal in `ApprovalsPage.jsx`.
- **Acceptance Criteria**: Commits `decision: 'APPROVED'` with required notes; updates UI.
- **Suggested Branch**: `feature/approval-decision-modal`
- **Testing Requirements**: Form submission test verifying decision payload.

### PR #066: Manager Rejection Action with Mandatory Feedback
- **Feature Area**: Approvals Decision
- **Objective**: Allow manager to reject request with required explanation notes.
- **Implementation Scope**: `handleConfirmDecision()` with `REJECTED`.
- **Acceptance Criteria**: Updates record status to `REJECTED`; records decision notes.
- **Suggested Branch**: `feature/approval-rejection-flow`
- **Testing Requirements**: Test asserting rejection notes cannot be empty.

### PR #067: Request Changes Action for Multi-Round Refinement
- **Feature Area**: Approvals Decision
- **Objective**: Action allowing manager to return request for employee amendment.
- **Implementation Scope**: Status `CHANGES_REQUESTED`.
- **Acceptance Criteria**: Sets status and notifies requester that modifications are needed.
- **Suggested Branch**: `feature/approval-request-changes`
- **Testing Requirements**: State transition test.

### PR #068: Automatic Workflow Resumption Linkage in Approval Service
- **Feature Area**: Workflow Integration & Critical Scenario
- **Objective**: When `Approval` has `workflow_execution_id`, decision triggers `workflowEngineService.resumeWorkflow()`.
- **Implementation Scope**: `ApprovalService.decideApproval()`.
- **Acceptance Criteria**: Approved request resumes paused workflow execution and marks completed.
- **Suggested Branch**: `feature/approval-workflow-resumption-hook`
- **Testing Requirements**: End-to-end service test verifying approval advances execution state.

### PR #069: Approver Auto-Assignment Hierarchy (Direct Manager vs Admin Fallback)
- **Feature Area**: Approvals Business Logic
- **Objective**: Assign approval to employee's direct manager; fallback to department head or admin if unassigned.
- **Implementation Scope**: `ApprovalService.createApproval()`.
- **Acceptance Criteria**: Looks up `user.manager_id`; assigns to manager automatically.
- **Suggested Branch**: `feature/approver-assignment-hierarchy`
- **Testing Requirements**: Service test verifying manager assignment logic.

### PR #070: Decision Outcome Notification to Employee
- **Feature Area**: Approvals & Notifications
- **Objective**: Send notification to requester immediately upon manager decision.
- **Implementation Scope**: `NotificationService.sendNotification()`.
- **Acceptance Criteria**: Dispatches `REQUEST_APPROVED` or `REQUEST_REJECTED` notification with manager notes.
- **Suggested Branch**: `feature/approval-decision-notification`
- **Testing Requirements**: Mock verify employee receives notification.

### PR #071: Urgent Approvals Dashboard Metric Card
- **Feature Area**: Dashboard Integration
- **Objective**: Surface pending approval count with red/emerald urgency badge on main dashboard.
- **Implementation Scope**: `DashboardPage.jsx`, `AnalyticsService.java`.
- **Acceptance Criteria**: Shows live count of pending approvals awaiting attention.
- **Suggested Branch**: `feature/dashboard-approvals-card`
- **Testing Requirements**: Dashboard render test with non-zero pending count.

### PR #072: Approvals Registry CSV Exporter
- **Feature Area**: Compliance & Export
- **Objective**: Export historical approval decisions, notes, and timestamps to downloadable CSV.
- **Implementation Scope**: `CsvExportButton.jsx` on `ApprovalsPage.jsx`.
- **Acceptance Criteria**: Downloads `approvals-registry.csv` with title, type, decision, notes, date.
- **Suggested Branch**: `feature/approvals-csv-export`
- **Testing Requirements**: Export trigger test.

---

## Domain 7: Notifications & Workspace Auditing (PRs #073 – #082)

### PR #073: Notification Entity with Type Categorization
- **Feature Area**: Notifications Model
- **Objective**: Model alerts for TASK_ASSIGNED, APPROVAL_REQUEST, REQUEST_APPROVED, REQUEST_REJECTED, WORKFLOW_COMPLETED, SYSTEM.
- **Implementation Scope**: `Notification.java`, `NotificationType.java`.
- **Acceptance Criteria**: Contains `title`, `message`, `is_read`, `link_url`, `created_at`.
- **Suggested Branch**: `feature/notification-entity-model`
- **Testing Requirements**: Entity persistence test.

### PR #074: Unread Notification Badge Counter in Top Navigation
- **Feature Area**: Frontend Navigation
- **Objective**: TopNav bell icon displaying live badge with count of unread notifications.
- **Implementation Scope**: `TopNav.jsx`, `/api/notifications/unread-count`.
- **Acceptance Criteria**: Badge appears with emerald badge count when `unreadCount > 0`; hidden when 0.
- **Suggested Branch**: `feature/notification-badge-counter`
- **Testing Requirements**: Component test asserting badge visibility based on unread count.

### PR #075: Top Navigation Notification Dropdown Popover
- **Feature Area**: Frontend Navigation
- **Objective**: Clicking bell icon opens flyout popover with recent notifications list.
- **Implementation Scope**: `TopNav.jsx`.
- **Acceptance Criteria**: Unread items highlighted with emerald tint; clicking an item navigates to link.
- **Suggested Branch**: `feature/notification-popover-ui`
- **Testing Requirements**: Interaction test verifying popover toggle and navigation.

### PR #076: Single Notification Mark-As-Read Endpoint & Handler
- **Feature Area**: Notifications API
- **Objective**: `PATCH /api/notifications/{id}/read` sets `is_read = true`.
- **Implementation Scope**: `NotificationService.markAsRead()`, `NotificationController.java`.
- **Acceptance Criteria**: Decrements unread badge count; enforces user ownership security.
- **Suggested Branch**: `feature/notification-mark-read-api`
- **Testing Requirements**: Controller test verifying mark-as-read security check.

### PR #077: Bulk Mark-All-Notifications-As-Read Action
- **Feature Area**: Notifications API
- **Objective**: `POST /api/notifications/mark-all-read` marks all user alerts read in one transaction.
- **Implementation Scope**: `NotificationService.markAllAsRead()`.
- **Acceptance Criteria**: All unread alerts updated; badge counter resets to 0.
- **Suggested Branch**: `feature/notification-mark-all-read`
- **Testing Requirements**: Service test verifying batch update.

### PR #078: Polling Interval for Live In-App Notification Refresh
- **Feature Area**: Frontend Notifications
- **Objective**: Background poll every 15 seconds to fetch new notifications without full page reload.
- **Implementation Scope**: `setInterval` in `TopNav.jsx`.
- **Acceptance Criteria**: Cleans up interval on component unmount; updates badge automatically.
- **Suggested Branch**: `feature/notification-live-polling`
- **Testing Requirements**: Vitest timer test for polling interval.

### PR #079: Audit Log Entity & Repository for Regulatory Compliance
- **Feature Area**: Compliance Model
- **Objective**: Entity tracking `organization_id`, `user_id`, `action`, `entity_type`, `entity_id`, `ip_address`, `details_json`.
- **Implementation Scope**: `AuditLog.java`, `AuditLogRepository.java`.
- **Acceptance Criteria**: Immutable historical entries indexed by organization ID and timestamp.
- **Suggested Branch**: `feature/audit-log-entity`
- **Testing Requirements**: Audit log query test.

### PR #080: Asynchronous Audit Logging Service
- **Feature Area**: Audit Architecture
- **Objective**: Service method `logAction()` that logs system actions without blocking user request threads.
- **Implementation Scope**: `AuditLogService.java`.
- **Acceptance Criteria**: Safe exception handling so logging failures never abort primary business transactions.
- **Suggested Branch**: `feature/audit-log-service`
- **Testing Requirements**: Service test verifying audit entry saved.

### PR #081: Live Workspace Audit Stream Widget on Dashboard
- **Feature Area**: Dashboard UI
- **Objective**: Card displaying recent 5 audit actions with timestamps and entity details.
- **Implementation Scope**: `DashboardPage.jsx`, `/api/audit/logs`.
- **Acceptance Criteria**: Renders action badges and JSON parameter previews cleanly.
- **Suggested Branch**: `feature/dashboard-audit-widget`
- **Testing Requirements**: Component test asserting audit list render.

### PR #082: Global Toast Notification Feedback System
- **Feature Area**: Frontend UI/UX
- **Objective**: Floating toast alerts displaying success, error, or notice messages with auto-dismiss.
- **Implementation Scope**: `Toast.jsx`.
- **Acceptance Criteria**: Emerald styling for success, coral for error; dismisses on click or timeout.
- **Suggested Branch**: `feature/toast-notification-system`
- **Testing Requirements**: Toast component render and dismiss test.

---

## Domain 8: Real-Time Analytics & Reporting (PRs #083 – #090)

### PR #083: Real-Time Analytics Aggregation Service
- **Feature Area**: Analytics Backend
- **Objective**: Compute live KPIs: total employees, active workflows, pending/completed tasks, overdue tasks, completion rate.
- **Implementation Scope**: `AnalyticsService.java`, `AnalyticsController.java`.
- **Acceptance Criteria**: No hardcoded dummy numbers; values computed from database counts.
- **Suggested Branch**: `feature/analytics-aggregation-service`
- **Testing Requirements**: Unit test asserting accurate metric calculation.

### PR #084: Monthly Task Velocity Bar Chart Component (Recharts)
- **Feature Area**: Analytics UI
- **Objective**: Bar chart showing tasks created vs resolved monthly in Emerald `#16A36A` and Deep Forest `#174C3C`.
- **Implementation Scope**: Recharts `<BarChart>` in `DashboardPage.jsx` and `AnalyticsPage.jsx`.
- **Acceptance Criteria**: Strictly compliant with non-blue design mandate; responsive container.
- **Suggested Branch**: `feature/analytics-task-velocity-chart`
- **Testing Requirements**: Component render test with Recharts mock data.

### PR #085: Workflow Health Donut Chart Component
- **Feature Area**: Analytics UI
- **Objective**: Pie/donut chart displaying workflow distribution across Active, Draft, and Paused states.
- **Implementation Scope**: Recharts `<PieChart>` in `DashboardPage.jsx`.
- **Acceptance Criteria**: Slices colored Emerald, Warm Amber, and Coral; zero blue colors.
- **Suggested Branch**: `feature/analytics-workflow-health-chart`
- **Testing Requirements**: Slice color verification test.

### PR #086: Departmental SLA Efficiency Comparison Table
- **Feature Area**: Analytics UI
- **Objective**: Table comparing resolution percentages and average turnaround hours across departments.
- **Implementation Scope**: `AnalyticsPage.jsx`.
- **Acceptance Criteria**: Progress bars showing department efficiency scores.
- **Suggested Branch**: `feature/analytics-department-efficiency`
- **Testing Requirements**: Table row rendering test.

### PR #087: Approval Decision Ratio Pie Chart Component
- **Feature Area**: Analytics UI
- **Objective**: Visual distribution of Approved vs Rejected vs Pending requests.
- **Implementation Scope**: `AnalyticsPage.jsx`.
- **Acceptance Criteria**: Clearly illustrates sign-off proportions with tooltips.
- **Suggested Branch**: `feature/analytics-approval-ratio-chart`
- **Testing Requirements**: Render test for chart components.

### PR #088: Monthly Automation Hours Saved Metric Calculator
- **Feature Area**: Analytics Business Logic
- **Objective**: Calculate estimated man-hours saved based on automated step executions.
- **Implementation Scope**: `AnalyticsPage.jsx`.
- **Acceptance Criteria**: Quantifies ROI (e.g. 348.5 hours saved) dynamically.
- **Suggested Branch**: `feature/analytics-roi-calculator`
- **Testing Requirements**: Calculation unit test.

### PR #089: Dashboard Metric KPI Summary CSV Export
- **Feature Area**: Reporting
- **Objective**: Download high-level dashboard KPI metrics as a CSV file.
- **Implementation Scope**: `DashboardPage.jsx`.
- **Acceptance Criteria**: Downloads `dashboard-kpi-summary.csv` with metric names and values.
- **Suggested Branch**: `feature/dashboard-kpi-csv-export`
- **Testing Requirements**: CSV export trigger test.

### PR #090: Monthly Workflow Throughput CSV Export
- **Feature Area**: Reporting
- **Objective**: Download monthly throughput data points for executive Excel analysis.
- **Implementation Scope**: `AnalyticsPage.jsx`.
- **Acceptance Criteria**: Downloads `monthly-workflow-throughput.csv` with month-by-month numbers.
- **Suggested Branch**: `feature/analytics-throughput-csv-export`
- **Testing Requirements**: CSV export trigger test.

---

## Domain 9: AI Workflow Assistant (PRs #091 – #096)

### PR #091: Natural-Language AI Workflow Assistant Controller & Service
- **Feature Area**: AI Assistant
- **Objective**: Endpoint `POST /api/ai/generate-workflow` accepting natural-language prompt.
- **Implementation Scope**: `AiWorkflowAssistantService.java`, `AiAssistantController.java`.
- **Acceptance Criteria**: Returns structured JSON with workflow name, category, nodes, and edges.
- **Suggested Branch**: `feature/ai-assistant-service`
- **Testing Requirements**: Service test verifying prompt generates valid node array.

### PR #092: Intelligent Rule-Based NLP Fallback Engine
- **Feature Area**: AI Assistant & Fallback
- **Objective**: Pattern-matching keyword engine that generates workflows when no external LLM key is configured.
- **Implementation Scope**: Keyword parser for "leave", "expense", "onboarding", "approval" in `AiWorkflowAssistantService.java`.
- **Acceptance Criteria**: Clearly labels `engine: "Rule-Based Natural Language Engine"`; never simulates fake API calls.
- **Suggested Branch**: `feature/ai-rule-based-engine`
- **Testing Requirements**: Keyword recognition unit tests for 4 standard business scenarios.

### PR #093: AI Assistant Interactive UI with Quick Suggestion Chips
- **Feature Area**: AI Assistant UI
- **Objective**: Prompt box with pre-configured template chips for instant testing.
- **Implementation Scope**: `AiAssistantPage.jsx`.
- **Acceptance Criteria**: Clicking chip populates prompt and triggers generation.
- **Suggested Branch**: `feature/ai-assistant-ui`
- **Testing Requirements**: Interaction test asserting prompt click populates input.

### PR #094: AI Generated Step Sequence Horizontal Timeline Preview
- **Feature Area**: AI Assistant UI
- **Objective**: Visual step-by-step card timeline showing generated Trigger, Action, Approval, and End nodes.
- **Implementation Scope**: `AiAssistantPage.jsx`.
- **Acceptance Criteria**: Color-coded step cards with directional connectors.
- **Suggested Branch**: `feature/ai-sequence-preview-ui`
- **Testing Requirements**: Component test asserting correct number of step cards rendered.

### PR #095: One-Click "Save AI Workflow to Database" Action
- **Feature Area**: AI Assistant Integration
- **Objective**: Button persisting generated definition directly to `/api/workflows`.
- **Implementation Scope**: `handleSaveWorkflow()` in `AiAssistantPage.jsx`.
- **Acceptance Criteria**: Saves workflow, displays success feedback, navigates to Workflows list.
- **Suggested Branch**: `feature/ai-save-workflow-action`
- **Testing Requirements**: API dispatch test verifying workflow persistence.

### PR #096: One-Click "Open in Visual Canvas Builder" Bridge
- **Feature Area**: AI Assistant & Builder Integration
- **Objective**: Bridge generated graph definition directly into Visual Workflow Builder for fine-tuning.
- **Implementation Scope**: `handleOpenInBuilder()` in `AiAssistantPage.jsx`.
- **Acceptance Criteria**: Navigates to `/builder` with generated nodes and connections loaded.
- **Suggested Branch**: `feature/ai-builder-bridge`
- **Testing Requirements**: Router state navigation test.

---

## Domain 10: Subscriptions, Billing & DevOps Infrastructure (PRs #097 – #104)

### PR #097: Subscription Plans & Usage Metering Service
- **Feature Area**: Billing Backend
- **Objective**: Track organization member seats, active workflows, and executions against plan limits.
- **Implementation Scope**: `SubscriptionService.java`, `SubscriptionController.java`.
- **Acceptance Criteria**: Returns current usage and ceiling limits for the active plan.
- **Suggested Branch**: `feature/subscription-metering-service`
- **Testing Requirements**: Service test verifying usage computation.

### PR #098: Plan Upgrade Interface & Simulated Gateway Checkout
- **Feature Area**: Billing UI
- **Objective**: Modal allowing users to upgrade plan (Starter, Professional, Enterprise) in test mode.
- **Implementation Scope**: `BillingPage.jsx`, `POST /api/subscriptions/upgrade`.
- **Acceptance Criteria**: Clearly marks `[Test Sandbox Mode]`; updates organization plan and allowances.
- **Suggested Branch**: `feature/billing-upgrade-modal`
- **Testing Requirements**: Form submission test verifying plan upgrade API call.

### PR #099: Commercial Billing Invoice History Table
- **Feature Area**: Billing UI
- **Objective**: Table listing historical paid invoices with download capabilities.
- **Implementation Scope**: `BillingPage.jsx`.
- **Acceptance Criteria**: Displays invoice ID, billing date, amount, and status pill.
- **Suggested Branch**: `feature/billing-invoice-history`
- **Testing Requirements**: Render test for invoice rows.

### PR #100: Multi-Container Docker Compose Architecture
- **Feature Area**: DevOps
- **Objective**: Orchestrate PostgreSQL 16, Spring Boot backend, and React/Nginx frontend.
- **Implementation Scope**: `docker-compose.yml`, `backend/Dockerfile`, `frontend/Dockerfile`.
- **Acceptance Criteria**: `docker compose up` brings up all 3 containers with healthchecks.
- **Suggested Branch**: `feature/devops-docker-compose`
- **Testing Requirements**: Docker Compose configuration syntax validation test.

### PR #101: GitHub Actions CI/CD Pipeline (Build, Test, Package)
- **Feature Area**: CI/CD
- **Objective**: Automated workflow running backend Maven tests and frontend Vite builds on push/PR.
- **Implementation Scope**: `.github/workflows/ci.yml`.
- **Acceptance Criteria**: Matrix execution for JDK 17 and Node.js 20.
- **Suggested Branch**: `ci/github-actions-pipeline`
- **Testing Requirements**: GitHub Actions workflow syntax validation.

### PR #102: GitHub Issue Templates & Codeowners Specification
- **Feature Area**: Project Governance
- **Objective**: Standardize bug reports, feature requests, and code ownership boundaries.
- **Implementation Scope**: `.github/ISSUE_TEMPLATE/`, `PULL_REQUEST_TEMPLATE.md`, `CODEOWNERS`.
- **Acceptance Criteria**: Templates provide structured input forms for issues and PR reviews.
- **Suggested Branch**: `docs/github-governance-templates`
- **Testing Requirements**: Markdown and YAML syntax validation.

### PR #103: Comprehensive Architecture & API Documentation
- **Feature Area**: Documentation
- **Objective**: Technical documentation detailing multi-tenant schema, security filters, and REST endpoints.
- **Implementation Scope**: `docs/ARCHITECTURE.md`, `docs/API_DOCUMENTATION.md`.
- **Acceptance Criteria**: Complete curl command examples and entity relationship diagrams.
- **Suggested Branch**: `docs/project-documentation`
- **Testing Requirements**: Documentation integrity check.

### PR #104: Interaction Audit Report & Test Verification Matrix
- **Feature Area**: Quality Assurance
- **Objective**: Audit of every clickable element, button, and form across the application verifying 100% functionality.
- **Implementation Scope**: `docs/INTERACTION_AUDIT.md`.
- **Acceptance Criteria**: Zero dead buttons; all actions connected to APIs or state handlers.
- **Suggested Branch**: `test/interaction-audit-report`
- **Testing Requirements**: Verification audit against all 12 modules.
