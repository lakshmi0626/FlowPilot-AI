# FlowPilot AI – Commercial SaaS Architectural Specification

## 1. Multi-Tenant Architectural Overview

FlowPilot AI implements a pooled multi-tenant architecture with logical data isolation enforced at the Spring Security and Spring Data JPA query layer. Every business entity contains a foreign key to `organizations.id`.

```mermaid
flowchart TD
    Client["React Frontend (SPA)"] -->|"HTTPS / REST (JWT Bearer)"| Gateway["Security Filter Chain"]
    Gateway -->|"Extract Claims & OrgId"| TenantFilter["JwtAuthenticationFilter"]
    TenantFilter -->|"Bind Tenant ID"| TenantCtx["TenantContext (ThreadLocal)"]
    
    TenantCtx --> AuthSvc["Auth & Security Service"]
    TenantCtx --> WfEngine["Workflow Execution Engine"]
    TenantCtx --> TaskSvc["Task & Collaboration Service"]
    TenantCtx --> ApprSvc["Approval Service"]
    TenantCtx --> AnalyticsSvc["Analytics Aggregator"]
    
    AuthSvc --> DB[("PostgreSQL / H2 DB (Tenant Scoped)")]
    WfEngine --> DB
    TaskSvc --> DB
    ApprSvc --> DB
    AnalyticsSvc --> DB
```

---

## 2. Critical End-to-End Business Scenario Flow

The core scenario mandated by the enterprise master specification:

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Company Admin (Eleanor)
    actor Manager as Manager (Marcus)
    actor Employee as Employee (David)
    participant Engine as Workflow Execution Engine
    participant DB as PostgreSQL Database
    
    Admin->>DB: 1. Register Acme Corporation & workspace
    Admin->>DB: 2. Invite Engineering Manager
    Manager->>Engine: 3. Design & Activate "Employee Leave Workflow"
    Employee->>Engine: 4. Submit Leave Request (3 Days Vacation)
    Engine->>DB: 5. Create WorkflowExecution (Status: RUNNING)
    Engine->>DB: 6. Advance to Approval Node -> Pause State (PAUSED_APPROVAL)
    Engine->>DB: 7. Dispatch Notification to Manager
    Manager->>Engine: 8. Review in Approval Center & Click "Approve"
    Engine->>DB: 9. Resume Workflow Execution
    Engine->>DB: 10. Execute HR Record Sync Task & Complete Flow
    Engine->>DB: 11. Dispatch Result Notification to Employee
    Employee->>DB: 12. View Live Dashboard KPI & Final Execution Status
```

---

## 3. Workflow Engine Execution State Machine

```mermaid
stateDiagram-v2
    [*] --> RUNNING: executeWorkflow()
    RUNNING --> RUNNING: Process START Node
    RUNNING --> RUNNING: Process TASK Node (Create Task entity)
    RUNNING --> RUNNING: Process NOTIFICATION Node (Dispatch alert)
    RUNNING --> PAUSED_APPROVAL: Encounter APPROVAL Node
    PAUSED_APPROVAL --> RUNNING: Manager Approves (resumeWorkflow)
    PAUSED_APPROVAL --> REJECTED: Manager Rejects
    RUNNING --> COMPLETED: Process END Node
    RUNNING --> FAILED: Exception / Invalid Topology
    COMPLETED --> [*]
    REJECTED --> [*]
    FAILED --> [*]
```

---

## 4. Role-Based Access Control (RBAC) Matrix

| Capability | SUPER_ADMIN | COMPANY_ADMIN | MANAGER | EMPLOYEE |
| :--- | :---: | :---: | :---: | :---: |
| Platform Tenant Management | YES | NO | NO | NO |
| Workspace Branding & Settings | YES | YES | NO | NO |
| Manage Billing & Subscriptions | YES | YES | NO | NO |
| Team Member Invitations | YES | YES | YES | NO |
| Create & Publish Workflows | YES | YES | YES | NO |
| Visual Workflow Builder Access | YES | YES | YES | YES |
| Review & Decide Approvals | YES | YES | YES | NO |
| Submit Leave / Expense Requests | YES | YES | YES | YES |
| Manage Assigned Tasks & Comments | YES | YES | YES | YES |
| View Organization Analytics | YES | YES | YES | YES |
| AI Workflow Generation | YES | YES | YES | YES |
