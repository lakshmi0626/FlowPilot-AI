import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import Sidebar from './components/Sidebar';
import TopNav from './components/TopNav';

import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import WorkflowsPage from './pages/WorkflowsPage';
import WorkflowBuilderPage from './pages/WorkflowBuilderPage';
import TasksPage from './pages/TasksPage';
import ApprovalsPage from './pages/ApprovalsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import AiAssistantPage from './pages/AiAssistantPage';
import EmployeesPage from './pages/EmployeesPage';
import BillingPage from './pages/BillingPage';
import SettingsPage from './pages/SettingsPage';

function ProtectedLayout({ children }) {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <TopNav />
        {children}
      </div>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Authenticated SaaS Platform Routes */}
          <Route path="/dashboard" element={<ProtectedLayout><DashboardPage /></ProtectedLayout>} />
          <Route path="/workflows" element={<ProtectedLayout><WorkflowsPage /></ProtectedLayout>} />
          <Route path="/builder" element={<ProtectedLayout><WorkflowBuilderPage /></ProtectedLayout>} />
          <Route path="/tasks" element={<ProtectedLayout><TasksPage /></ProtectedLayout>} />
          <Route path="/approvals" element={<ProtectedLayout><ApprovalsPage /></ProtectedLayout>} />
          <Route path="/analytics" element={<ProtectedLayout><AnalyticsPage /></ProtectedLayout>} />
          <Route path="/ai-assistant" element={<ProtectedLayout><AiAssistantPage /></ProtectedLayout>} />
          <Route path="/team" element={<ProtectedLayout><EmployeesPage /></ProtectedLayout>} />
          <Route path="/billing" element={<ProtectedLayout><BillingPage /></ProtectedLayout>} />
          <Route path="/settings" element={<ProtectedLayout><SettingsPage /></ProtectedLayout>} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
