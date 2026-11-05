# FlowPilot AI
### Intelligent Business Workflow Automation Platform
**Commercial Enterprise Multi-Tenant SaaS Product**

[![CI/CD Pipeline](https://github.com/FlowPilot-AI/FlowPilot-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/FlowPilot-AI/FlowPilot-AI/actions)
[![License](https://img.shields.io/badge/license-Commercial-16A36A.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-174C3C.svg)](https://spring.io)
[![React](https://img.shields.io/badge/React-18-16A36A.svg)](https://react.dev)
[![Design Compliance](https://img.shields.io/badge/Visual%20Design-Strict%20Non--Blue%20Compliant-16A36A.svg)](#strict-visual-design-system)

---

## 1. Product Overview

**FlowPilot AI** is a complete, commercially presentable business workflow automation platform built for modern organizations. It unifies visual graph workflow modeling, human-in-the-loop decision checkpoints, automated task dispatching, team collaboration, and real-time execution analytics within a multi-tenant business SaaS architecture.

---

## 2. Strict Visual Design System (Non-Blue Mandate)

FlowPilot AI strictly adheres to the enterprise aesthetic requirement where **blue colors are completely prohibited**:
- **Primary Emerald**: `#16A36A`
- **Secondary Forest**: `#174C3C`
- **Background**: `#F7F8F5`
- **Cards & Surfaces**: `#FFFFFF`
- **Text Primary**: `#202820`
- **Text Secondary**: `#737B73`
- **Borders**: `#E5E9E2`
- **Success**: `#269B64`
- **Warning**: `#E5A43B`
- **Danger**: `#D9534F`

---

## 3. Technology Stack

- **Frontend**: React 18, Vite, React Router v6, Axios, Recharts, Lucide Icons, Modern Responsive CSS.
- **Backend**: Java 17+, Spring Boot 3.3.4, Spring Security 6, JWT (JJWT 0.12.5), Spring Data JPA, Bean Validation, Maven.
- **Database**: PostgreSQL / H2 (PostgreSQL compatibility mode for zero-dependency local evaluation), Flyway migrations.
- **DevOps**: Docker, Docker Compose, GitHub Actions CI/CD.

---

## 4. User Roles & Demo Credentials

The platform seeds demo tenants (`Acme Corporation` and `TechFlow Innovations`) with role-based personas:

| Role | Name | Email | Password | Primary Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **COMPANY_ADMIN** | Eleanor Pemberton | `admin@acme.com` | `Password123!` | Full workspace administration, team management, billing, settings |
| **MANAGER** | Marcus Sterling | `manager@acme.com` | `Password123!` | Workflow creation, review & sign-off approvals, assign tasks |
| **EMPLOYEE** | David Chen | `employee@acme.com` | `Password123!` | Submit requests, execute assigned tasks, receive notifications |
| **SUPER_ADMIN** | Alexander Vance | `superadmin@flowpilot.ai` | `Password123!` | Multi-tenant platform management, global subscriptions |

> **Pro-Tip**: The application includes a **1-click Persona Switcher** dropdown in the top navigation bar, allowing immediate switching between Admin, Manager, and Employee for end-to-end testing.

---

## 5. Critical End-to-End Business Scenario

FlowPilot AI executes this exact scenario across the frontend, backend engine, and database:

1. **Company Registers**: Admin registers Acme Corporation via the registration portal.
2. **Admin Sets Workspace**: Admin invites manager and configures departments.
3. **Manager Creates Workflow**: Manager creates the **Employee Leave Request Workflow**.
4. **Employee Submits Request**: Employee (David) files a 3-day leave request.
5. **Backend Engine Executes**: Real Spring Boot execution engine initiates `WorkflowExecution`.
6. **Engine Pauses at Approval Node**: State transitions to `PAUSED_APPROVAL`.
7. **Manager Receives Notification**: Manager (Marcus) sees red badge alert in top navigation.
8. **Manager Reviews & Approves**: Manager clicks **Approve** in the Approval Center with sign-off notes.
9. **Workflow Resumes**: Engine resumes execution, creates automated HR record synchronization task, and concludes flow (`COMPLETED`).
10. **Employee Receives Result**: David receives notification confirming approval.
11. **Database Persistence**: State transitions and audit logs are recorded.
12. **Dashboard Updates**: Real-time KPI charts reflect resolved tasks and updated completion rate.

---

## 6. How to Run the Application

### Option A: Local Development (Instant Out-of-the-Box)

#### 1. Start the Spring Boot Backend (Port 8080)
```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26"  # or your installed JDK 17+ path
& "C:\Tools\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
```
*The backend automatically starts on `http://localhost:8080`, initializes the database schema, and seeds demo tenants.*

#### 2. Start the React Frontend (Port 5173)
```powershell
cd frontend
npm run dev
```
*Open `http://localhost:5173` in your browser. Use the 1-click persona buttons on the login page to enter immediately.*

---

### Option B: Production Docker Compose
```bash
docker compose up --build
```
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/api`
- PostgreSQL: `localhost:5432`

---

## 7. Automated Testing

### Backend Unit & Integration Tests:
```powershell
cd backend
mvn test
```

### Frontend Vitest Suite:
```powershell
cd frontend
npm test
```

### Production Build Verification:
```powershell
cd frontend
npm run build
```

---

## 8. Project Structure

```text
FlowPilot-AI/
  ├── backend/
  │   ├── src/main/java/com/flowpilot/
  │   │   ├── controller/      # REST API Controllers (12 Controllers)
  │   │   ├── dto/             # Request & Response Data Transfer Objects
  │   │   ├── model/           # Relational JPA Entities & Enums
  │   │   ├── repository/      # Tenant-isolated Spring Data Repositories
  │   │   ├── security/        # JWT Token Provider, Filter & SecurityConfig
  │   │   └── service/         # WorkflowEngine, Approval, Analytics Services
  │   ├── src/main/resources/  # application.yml (H2 & PostgreSQL profiles)
  │   ├── pom.xml              # Maven dependencies & plugins
  │   └── Dockerfile           # Multi-stage JDK build container
  ├── frontend/
  │   ├── src/
  │   │   ├── components/      # Sidebar, TopNav, Modal, Toast, CsvExportButton
  │   │   ├── context/         # AuthContext with persona quick-switcher
  │   │   ├── pages/           # Landing, Dashboard, Workflows, Builder, Tasks, etc.
  │   │   ├── services/        # Axios API client with Bearer interceptor
  │   │   ├── App.jsx          # Protected route layout & BrowserRouter
  │   │   └── index.css        # Strict Non-Blue Design System styling
  │   ├── package.json
  │   ├── vite.config.js
  │   └── Dockerfile
  ├── database/
  │   ├── migrations/          # V1__init_schema.sql (Flyway DDL)
  │   └── seeds/               # V2__seed_demo_data.sql (Demo tenants)
  ├── docs/
  │   ├── ARCHITECTURE.md      # Multi-tenant sequence & architecture diagrams
  │   ├── API_DOCUMENTATION.md # REST endpoint specs and payloads
  │   ├── PR_BACKLOG.md        # 100+ genuinely distinct PR engineering tasks
  │   └── INTERACTION_AUDIT.md # Audit verifying 176/176 clickable elements
  ├── .github/
  │   ├── workflows/ci.yml     # Automated CI/CD build matrix
  │   └── ISSUE_TEMPLATE/      # Bug report & feature request templates
  ├── docker-compose.yml       # Production multi-service orchestration
  └── README.md
```
