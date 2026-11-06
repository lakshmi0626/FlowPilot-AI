# Contributing to FlowPilot AI

Thank you for contributing to FlowPilot AI – Intelligent Business Workflow Automation SaaS Platform! This document outlines our engineering standards, branching models, coding conventions, and pull request workflows.

---

## 1. Branching Strategy

We follow a structured Gitflow model optimized for enterprise SaaS continuous integration:

- **`main`**: Production-ready branch. Only receives fast-forward merges from `develop` via release tags.
- **`develop`**: Primary integration branch. All feature branches, bugfixes, and documentation updates target `develop`.
- **`feature/<module-name>`**: New features and functional enhancements (e.g., `feature/workflow-engine`, `feature/tasks`).
- **`bugfix/<issue-name>`**: Non-critical bug fixes and logic patches (e.g., `bugfix/spring-boot-test-profile`).
- **`hotfix/<issue-name>`**: Urgent production fixes branched directly from `main`.
- **`chore/<task-name>`**: Repository governance, maintenance, or dependency housekeeping (e.g., `chore/repository-governance`).
- **`docs/<topic>`**: Documentation guides and architecture roadmaps (e.g., `docs/project-documentation`).

---

## 2. Conventional Commits Standard

Every commit must adhere strictly to the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```text
<type>(<scope>): <short description in present imperative tense>

[optional body explaining motivation and context]

[optional footer(s), e.g., Closes #123]
```

### Allowed Types:
- **`feat`**: A new feature for the user or platform.
- **`fix`**: A bug fix.
- **`test`**: Adding missing tests or correcting existing tests.
- **`docs`**: Documentation only changes.
- **`style`**: Changes that do not affect code meaning (white-space, formatting).
- **`refactor`**: A code change that neither fixes a bug nor adds a feature.
- **`perf`**: A code change that improves performance.
- **`ci`**: Changes to CI/CD configuration files and scripts.
- **`chore`**: Maintenance tasks, dependencies, build tool updates.

### Examples:
- `feat(engine): add recursive node traversal in resumeWorkflow`
- `fix(auth): prevent null pointer on unauthenticated tenant context lookup`
- `test(tasks): add unit tests for task priority and status transitions`
- `docs(pr): update PR roadmap with approval delegation scope`

---

## 3. Strict UI Design System: Zero Blue Rule

FlowPilot AI operates under a strict enterprise visual design rule:
> **BLUE, NAVY, SKY BLUE, AND CYAN ARE STRICTLY PROHIBITED.**

### Brand Color Tokens:
- **Primary Emerald**: `#16A36A`
- **Deep Forest Green**: `#174C3C`
- **Soft Background**: `#F7F8F5`
- **Pure White Cards**: `#FFFFFF`
- **Charcoal Text**: `#202820`
- **Muted Subtext**: `#737B73`
- **Card Borders**: `#E5E9E2`
- **Success Accent**: `#269B64`
- **Warning Amber**: `#E5A43B`
- **Danger Red**: `#D9534F`

Never introduce CSS classes, inline styles, SVG colors, or charts containing blue hues (`#0000FF`, `#1E40AF`, `#0284C7`, `#06B6D4`, etc.).

---

## 4. Multi-Tenant Architecture & Data Security Rules

1. **Tenant Isolation**: Every database entity (except super-admin platform settings) must link to `organization_id`.
2. **Context Resolution**: Always access tenant identity via `TenantContext.getCurrentTenant()`.
3. **Audit Logging**: Every mutating action (create, update, delete, approve) must record an immutable audit log entry via `AuditLogService.logAction()`.
4. **No Plaintext Passwords**: All user credentials must be hashed using BCrypt factor 12 before persistence.

---

## 5. Development & Testing Workflow

### Backend (Spring Boot 3.3.4, Java 17+):
```bash
cd backend
mvn clean test
mvn compile
```
- Tests must use `@Profile("test")` and Mockito subclass mock maker (`mock-maker-subclass`).
- Never skip tests (`-DskipTests`) when pushing to remote.

### Frontend (React 18 + Vite):
```bash
cd frontend
npm install
npm test -- --run
npm run build
```
- Verify zero dead buttons or broken routing.
- Check responsive layouts across mobile, tablet, and desktop breakpoints.

---

## 6. Pull Request Review Guidelines

1. **Branch Source**: Always branch from up-to-date `develop`.
2. **Target Branch**: Set PR target branch to `develop` (never directly to `main`).
3. **PR Template**: Complete all sections of `.github/PULL_REQUEST_TEMPLATE.md`.
4. **Verification Evidence**: Include test results, build outputs, and manual interaction confirmations.
5. **Clean History**: Do not create empty, duplicate, or artificially fabricated commits.
