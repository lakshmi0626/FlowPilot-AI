import React, { useState, useEffect } from 'react';
import { Bell, Search, LogOut, User, Check, Sparkles, Building2, ChevronDown } from 'lucide-react';
import { useAuth, DEMO_USERS } from '../context/AuthContext';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';

export default function TopNav() {
  const { user, logout, switchRole } = useAuth();
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showRoleMenu, setShowRoleMenu] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 15000);
    return () => clearInterval(interval);
  }, [user]);

  const fetchNotifications = async () => {
    try {
      const resp = await api.get('/notifications?unreadOnly=false');
      setNotifications(resp.data);
      const unread = resp.data.filter(n => !n.isRead).length;
      setUnreadCount(unread);
    } catch (err) {
      // Fallback demo notifications
      const demoNotifs = [
        { id: 1, title: 'Leave Approval Requested', message: 'David Chen submitted leave request (3 days)', isRead: false, linkUrl: '/approvals' },
        { id: 2, title: 'Infrastructure Task Assigned', message: 'Assigned to: Audit Q3 Cloud Costs', isRead: false, linkUrl: '/tasks' },
        { id: 3, title: 'Workflow Ready', message: 'Leave Request Workflow v1 is active', isRead: true, linkUrl: '/workflows' }
      ];
      setNotifications(demoNotifs);
      setUnreadCount(demoNotifs.filter(n => !n.isRead).length);
    }
  };

  const handleMarkAsRead = async (id, linkUrl) => {
    try {
      await api.patch(`/notifications/${id}/read`);
    } catch (e) {
      // ignore
    }
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
    setUnreadCount(prev => Math.max(0, prev - 1));
    if (linkUrl) {
      setShowNotifications(false);
      navigate(linkUrl);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await api.post('/notifications/mark-all-read');
    } catch (e) {
      // ignore
    }
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    setUnreadCount(0);
  };

  return (
    <header style={{
      height: '68px',
      backgroundColor: '#FFFFFF',
      borderBottom: '1px solid var(--border)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0 32px',
      position: 'sticky',
      top: 0,
      zIndex: 90
    }}>
      {/* Search Bar */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', width: '380px', position: 'relative' }}>
        <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px' }} />
        <input
          type="text"
          placeholder="Search workflows, tasks, team..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="form-input"
          style={{ paddingLeft: '36px', height: '38px', fontSize: '13px', backgroundColor: 'var(--bg-main)' }}
        />
      </div>

      {/* Right Controls */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        {/* Test Persona Switcher (For seamless End-to-End scenario verification) */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setShowRoleMenu(!showRoleMenu)}
            className="btn btn-secondary btn-sm"
            style={{ fontSize: '12px', display: 'flex', alignItems: 'center', gap: '6px' }}
            title="Switch User Persona to test workflows & approvals"
          >
            <User size={14} color="#16A36A" />
            <span>Persona: <strong>{user?.role?.replace('_', ' ') || 'User'}</strong></span>
            <ChevronDown size={12} color="var(--text-muted)" />
          </button>

          {showRoleMenu && (
            <div style={{
              position: 'absolute',
              right: 0,
              top: '42px',
              width: '240px',
              backgroundColor: '#FFFFFF',
              borderRadius: 'var(--radius-md)',
              boxShadow: 'var(--shadow-xl)',
              border: '1px solid var(--border)',
              padding: '6px',
              zIndex: 200
            }}>
              <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', padding: '6px 10px' }}>
                SWITCH ACTIVE PERSONA
              </div>
              {Object.keys(DEMO_USERS).map((key) => {
                const u = DEMO_USERS[key];
                const active = user?.email === u.email;
                return (
                  <button
                    key={key}
                    onClick={() => {
                      switchRole(key);
                      setShowRoleMenu(false);
                    }}
                    style={{
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '8px 10px',
                      borderRadius: '6px',
                      border: 'none',
                      backgroundColor: active ? 'var(--primary-light)' : 'transparent',
                      color: active ? 'var(--secondary)' : 'var(--text-main)',
                      fontSize: '13px',
                      fontWeight: 600,
                      cursor: 'pointer',
                      textAlign: 'left'
                    }}
                  >
                    <div>
                      <div>{u.firstName} {u.lastName}</div>
                      <span style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 400 }}>
                        {u.role.replace('_', ' ')}
                      </span>
                    </div>
                    {active && <Check size={14} color="#16A36A" />}
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Notifications Bell */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            style={{
              width: '38px',
              height: '38px',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border)',
              backgroundColor: '#FFFFFF',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              position: 'relative'
            }}
          >
            <Bell size={18} color="var(--secondary)" />
            {unreadCount > 0 && (
              <span style={{
                position: 'absolute',
                top: '-4px',
                right: '-4px',
                backgroundColor: 'var(--primary)',
                color: '#FFFFFF',
                borderRadius: 'var(--radius-full)',
                fontSize: '10px',
                fontWeight: 700,
                width: '18px',
                height: '18px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 2px 4px rgba(22, 163, 106, 0.4)'
              }}>
                {unreadCount}
              </span>
            )}
          </button>

          {showNotifications && (
            <div style={{
              position: 'absolute',
              right: 0,
              top: '46px',
              width: '320px',
              backgroundColor: '#FFFFFF',
              borderRadius: 'var(--radius-lg)',
              boxShadow: 'var(--shadow-xl)',
              border: '1px solid var(--border)',
              zIndex: 200,
              overflow: 'hidden'
            }}>
              <div style={{
                padding: '14px 18px',
                borderBottom: '1px solid var(--border)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                backgroundColor: '#FAFBFA'
              }}>
                <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--secondary)' }}>Notifications</span>
                {unreadCount > 0 && (
                  <button
                    onClick={handleMarkAllRead}
                    style={{ background: 'none', border: 'none', fontSize: '12px', color: 'var(--primary)', cursor: 'pointer', fontWeight: 600 }}
                  >
                    Mark all read
                  </button>
                )}
              </div>
              <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
                {notifications.length === 0 ? (
                  <div style={{ padding: '24px', textAlign: 'center', color: 'var(--text-muted)', fontSize: '13px' }}>
                    No notifications
                  </div>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.id}
                      onClick={() => handleMarkAsRead(n.id, n.linkUrl)}
                      style={{
                        padding: '12px 16px',
                        borderBottom: '1px solid var(--border)',
                        backgroundColor: n.isRead ? '#FFFFFF' : 'var(--primary-light)',
                        cursor: 'pointer',
                        transition: 'background-color 0.15s ease'
                      }}
                    >
                      <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-main)', marginBottom: '3px' }}>
                        {n.title}
                      </div>
                      <div style={{ fontSize: '12px', color: 'var(--text-muted)', lineHeight: '1.4' }}>
                        {n.message}
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* User Pill & Logout */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          paddingLeft: '8px',
          borderLeft: '1px solid var(--border)'
        }}>
          <div style={{
            width: '36px',
            height: '36px',
            borderRadius: 'var(--radius-full)',
            backgroundColor: 'var(--secondary)',
            color: '#FFFFFF',
            fontWeight: 700,
            fontSize: '13px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            {user?.firstName?.[0] || 'U'}
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-main)', lineHeight: '1.2' }}>
              {user?.firstName} {user?.lastName}
            </span>
            <span style={{ fontSize: '11px', color: 'var(--primary)', fontWeight: 600 }}>
              {user?.role?.replace('_', ' ')}
            </span>
          </div>

          <button
            onClick={logout}
            className="btn btn-secondary btn-sm"
            style={{ padding: '6px 8px', border: '1px solid var(--border)' }}
            title="Log Out"
          >
            <LogOut size={15} color="var(--danger)" />
          </button>
        </div>
      </div>
    </header>
  );
}
