# FlowPilot AI – Commercial REST API Documentation

Base URL: `http://localhost:8080/api`

All authenticated endpoints require an HTTP header:
```http
Authorization: Bearer <jwt-token>
```

---

## 1. Authentication Endpoints

### Login
`POST /api/auth/login`
```json
{
  "email": "admin@acme.com",
  "password": "Password123!"
}
```
**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "id": 2,
  "email": "admin@acme.com",
  "firstName": "Eleanor",
  "lastName": "Pemberton",
  "role": "COMPANY_ADMIN",
  "organizationId": 1,
  "organizationName": "Acme Corporation",
  "organizationSlug": "acme-corp"
}
```

### Register Company & Workspace
`POST /api/auth/register-company`
```json
{
  "companyName": "Acme Corporation",
  "slug": "acme-corp",
  "firstName": "Eleanor",
  "lastName": "Pemberton",
  "email": "admin@acme.com",
  "password": "Password123!"
}
```

---

## 2. Workflows & Execution Engine

### List Workflows
`GET /api/workflows`

### Create Workflow
`POST /api/workflows`
```json
{
  "name": "Employee Leave Request Workflow",
  "description": "Multi-tier approval pipeline",
  "category": "HUMAN_RESOURCES",
  "status": "ACTIVE",
  "definitionJson": "{\"nodes\":[],\"edges\":[]}"
}
```

### Trigger Workflow Execution
`POST /api/executions/workflow/{workflowId}/execute`
```json
{
  "payloadJson": "{\"requester\":\"David Chen\",\"days\":3}"
}
```
**Response (200 OK)**:
```json
{
  "id": 101,
  "organizationId": 1,
  "workflowId": 1,
  "triggeredByUserId": 4,
  "status": "PAUSED_APPROVAL",
  "currentNodeKey": "appr_1",
  "startedAt": "2026-11-05T10:45:00"
}
```

### Get Execution Logs
`GET /api/executions/{id}/logs`

---

## 3. Approvals & Human Decisions

### List Approvals
`GET /api/approvals?status=PENDING`

### Decide Approval
`POST /api/approvals/{id}/decide`
```json
{
  "decision": "APPROVED",
  "decisionNotes": "Approved for systems architecture summit."
}
```

---

## 4. Task Management

### List Tasks
`GET /api/tasks?status=IN_PROGRESS&priority=HIGH`

### Create Task
`POST /api/tasks`
```json
{
  "title": "Audit Q3 Cloud Infrastructure Costs",
  "description": "Evaluate Kubernetes reserved instances",
  "assignedToUserId": 4,
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2026-11-15T17:00:00"
}
```

---

## 5. AI Workflow Assistant

### Generate Workflow Graph
`POST /api/ai/generate-workflow`
```json
{
  "prompt": "Create an employee leave approval workflow with manager approval."
}
```
**Response (200 OK)**:
```json
{
  "engine": "Rule-Based Natural Language Engine",
  "name": "Employee Leave Approval Process",
  "category": "HUMAN_RESOURCES",
  "definition": {
    "nodes": [...],
    "edges": [...]
  }
}
```

---

## 6. Real-Time Analytics

### Get Executive Summary
`GET /api/analytics/summary`
```json
{
  "totalEmployees": 4,
  "activeWorkflows": 2,
  "pendingTasks": 2,
  "completedTasks": 2,
  "pendingApprovals": 2,
  "workflowCompletionRate": 94.2
}
```
