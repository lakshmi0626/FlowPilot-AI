import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  GitBranch,
  Workflow,
  CheckSquare,
  ClipboardCheck,
  BarChart3,
  Sparkles,
  Users,
  CreditCard,
  Settings,
  ShieldCheck,
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { user } = useAuth();
  const role = user?.role || 'EMPLOYEE';

  const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/workflows', label: 'Workflows', icon: GitBranch },
    { to: '/builder', label: 'Visual Builder', icon: Workflow },
    { to: '/tasks', label: 'Tasks', icon: CheckSquare },
    { to: '/approvals', label: 'Approval Center', icon: ClipboardCheck },
    { to: '/analytics', label: 'Analytics', icon: BarChart3 },
    { to: '/ai-assistant', label: 'AI Assistant', icon: Sparkles, badge: 'Smart' },
  ];

  const adminItems = [
    { to: '/team', label: 'Team & Roles', icon: Users },
    { to: '/billing', label: 'Subscription', icon: CreditCard },
    { to: '/settings', label: 'Workspace Settings', icon: Settings },
  ];

  return (
    <aside style={{
      width: '260px',
      backgroundColor: '#FFFFFF',
      borderRight: '1px solid var(--border)',
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      position: 'sticky',
      top: 0,
      zIndex: 100
    }}>
      {/* Brand Header */}
      <div style={{
        padding: '24px 20px',
        borderBottom: '1px solid var(--border)',
        display: 'flex',
        alignItems: 'center',
        gap: '12px'
      }}>
        <div style={{
          width: '38px',
          height: '38px',
          borderRadius: '10px',
          backgroundColor: 'var(--secondary)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#FFFFFF'
        }}>
          <Sparkles size={20} color="#16A36A" />
        </div>
        <div>
          <h2 style={{ fontSize: '18px', fontWeight: 800, color: 'var(--secondary)', letterSpacing: '-0.03em' }}>
            FlowPilot<span style={{ color: 'var(--primary)', fontWeight: 700 }}>.AI</span>
          </h2>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 500 }}>
            Commercial Enterprise
          </span>
        </div>
      </div>

      {/* Navigation Links */}
      <div style={{ flex: 1, padding: '16px 12px', overflowY: 'auto' }}>
        <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em', padding: '6px 12px 10px' }}>
          Operations
        </div>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                style={({ isActive }) => ({
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '10px 14px',
                  borderRadius: 'var(--radius-md)',
                  fontSize: '14px',
                  fontWeight: 600,
                  color: isActive ? 'var(--secondary)' : 'var(--text-muted)',
                  backgroundColor: isActive ? 'var(--primary-light)' : 'transparent',
                  border: isActive ? '1px solid var(--primary-border)' : '1px solid transparent',
                  textDecoration: 'none',
                  transition: 'all 0.15s ease'
                })}
              >
                <Icon size={18} color="currentColor" />
                <span style={{ flex: 1 }}>{item.label}</span>
                {item.badge && (
                  <span style={{
                    fontSize: '10px',
                    padding: '2px 6px',
                    backgroundColor: 'var(--primary)',
                    color: '#FFFFFF',
                    borderRadius: 'var(--radius-full)',
                    fontWeight: 700
                  }}>
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Administration Section */}
        {(role === 'COMPANY_ADMIN' || role === 'SUPER_ADMIN' || role === 'MANAGER') && (
          <div style={{ marginTop: '24px' }}>
            <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em', padding: '6px 12px 10px' }}>
              Administration
            </div>
            <nav style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {adminItems.map((item) => {
                const Icon = item.icon;
                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    style={({ isActive }) => ({
                      display: 'flex',
                      alignItems: 'center',
                      gap: '12px',
                      padding: '10px 14px',
                      borderRadius: 'var(--radius-md)',
                      fontSize: '14px',
                      fontWeight: 600,
                      color: isActive ? 'var(--secondary)' : 'var(--text-muted)',
                      backgroundColor: isActive ? 'var(--primary-light)' : 'transparent',
                      border: isActive ? '1px solid var(--primary-border)' : '1px solid transparent',
                      textDecoration: 'none',
                      transition: 'all 0.15s ease'
                    })}
                  >
                    <Icon size={18} color="currentColor" />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
            </nav>
          </div>
        )}
      </div>

      {/* Tenant Indicator & Plan Badge */}
      <div style={{
        padding: '16px',
        borderTop: '1px solid var(--border)',
        backgroundColor: '#FAFBFA'
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '6px'
        }}>
          <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)' }}>TENANT WORKSPACE</span>
          <span className="badge badge-success" style={{ fontSize: '10px', padding: '1px 7px' }}>Professional</span>
        </div>
        <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--secondary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
          {user?.organizationName || 'Acme Corporation'}
        </div>
        <div style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px', marginTop: '3px' }}>
          <ShieldCheck size={12} color="#16A36A" /> Tenant Isolation: Active
        </div>
      </div>
    </aside>
  );
}
