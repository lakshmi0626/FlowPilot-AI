import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Sparkles, ArrowRight, ShieldCheck, Lock, Mail, AlertCircle } from 'lucide-react';
import { useAuth, DEMO_USERS } from '../context/AuthContext';
import Modal from '../components/Modal';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@acme.com');
  const [password, setPassword] = useState('Password123!');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [resetEmail, setResetEmail] = useState('');
  const [resetSuccess, setResetSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err) {
      setError('Invalid email or password. You can also use the demo persona buttons below.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = (personaKey) => {
    const p = DEMO_USERS[personaKey];
    if (p) {
      setEmail(p.email);
      setPassword('Password123!');
      login(p.email, 'Password123!').then(() => navigate('/dashboard'));
    }
  };

  const handleResetPassword = (e) => {
    e.preventDefault();
    setResetSuccess(true);
    setTimeout(() => {
      setShowForgotModal(false);
      setResetSuccess(false);
    }, 1500);
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: 'var(--bg-main)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '32px 16px'
    }}>
      {/* Brand Header */}
      <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '28px' }}>
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
        <span style={{ fontSize: '22px', fontWeight: 800, color: 'var(--secondary)', letterSpacing: '-0.03em' }}>
          FlowPilot<span style={{ color: 'var(--primary)', fontWeight: 700 }}>.AI</span>
        </span>
      </Link>

      <div className="card" style={{ maxWidth: '440px', width: '100%', padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <h2 style={{ fontSize: '22px', color: 'var(--secondary)', marginBottom: '6px' }}>Welcome Back</h2>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Sign in to access your organization's workflow hub
          </p>
        </div>

        {error && (
          <div style={{
            padding: '10px 14px',
            backgroundColor: 'var(--danger-bg)',
            border: '1px solid #F3C4C3',
            borderRadius: 'var(--radius-md)',
            color: 'var(--danger)',
            fontSize: '13px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            marginBottom: '18px'
          }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Corporate Email</label>
            <div style={{ position: 'relative' }}>
              <Mail size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '13px' }} />
              <input
                type="email"
                required
                className="form-input"
                style={{ paddingLeft: '36px' }}
                placeholder="name@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
              <label className="form-label" style={{ margin: 0 }}>Password</label>
              <button
                type="button"
                onClick={() => setShowForgotModal(true)}
                style={{ background: 'none', border: 'none', fontSize: '12px', color: 'var(--primary)', cursor: 'pointer', fontWeight: 600 }}
              >
                Forgot password?
              </button>
            </div>
            <div style={{ position: 'relative' }}>
              <Lock size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '13px' }} />
              <input
                type="password"
                required
                className="form-input"
                style={{ paddingLeft: '36px' }}
                placeholder="••••••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '6px' }}
          >
            {loading ? 'Authenticating...' : 'Sign In to Workspace'}
          </button>
        </form>

        {/* Demo Quick Login Helper */}
        <div style={{ marginTop: '28px', paddingTop: '20px', borderTop: '1px solid var(--border)' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '10px', textAlign: 'center' }}>
            DEMO ONE-CLICK QUICK PERSONA
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
            <button
              type="button"
              onClick={() => handleQuickLogin('ADMIN')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '11px', padding: '6px 4px' }}
            >
              Admin
            </button>
            <button
              type="button"
              onClick={() => handleQuickLogin('MANAGER')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '11px', padding: '6px 4px' }}
            >
              Manager
            </button>
            <button
              type="button"
              onClick={() => handleQuickLogin('EMPLOYEE')}
              className="btn btn-secondary btn-sm"
              style={{ fontSize: '11px', padding: '6px 4px' }}
            >
              Employee
            </button>
          </div>
        </div>

        <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '13px', color: 'var(--text-muted)' }}>
          Don't have a workspace?{' '}
          <Link to="/register" style={{ fontWeight: 600, color: 'var(--primary)' }}>
            Register Organization
          </Link>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginTop: '24px', fontSize: '12px', color: 'var(--text-muted)' }}>
        <ShieldCheck size={14} color="#16A36A" />
        <span>End-to-End JWT Authentication with Tenant Scope</span>
      </div>

      {/* Forgot Password Modal */}
      <Modal
        isOpen={showForgotModal}
        onClose={() => setShowForgotModal(false)}
        title="Reset Account Password"
      >
        {resetSuccess ? (
          <div style={{ textAlign: 'center', padding: '16px 0' }}>
            <p style={{ fontSize: '14px', color: 'var(--primary)', fontWeight: 600 }}>
              Password reset link has been dispatched to {resetEmail || email}!
            </p>
          </div>
        ) : (
          <form onSubmit={handleResetPassword}>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginBottom: '16px' }}>
              Enter your corporate email address to receive secure reset credentials.
            </p>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                required
                className="form-input"
                placeholder="name@company.com"
                value={resetEmail}
                onChange={(e) => setResetEmail(e.target.value)}
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
              Send Reset Instructions
            </button>
          </form>
        )}
      </Modal>
    </div>
  );
}
