import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Sparkles, Building2, User, Mail, Lock, ShieldCheck, Check } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { registerCompany } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    companyName: '',
    slug: '',
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleCompanyNameChange = (e) => {
    const val = e.target.value;
    const generatedSlug = val.toLowerCase().replace(/[^a-z0-9]/g, '-').replace(/-+/g, '-').slice(0, 30);
    setFormData({
      ...formData,
      companyName: val,
      slug: generatedSlug
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await registerCompany(formData);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to register workspace. Please check fields.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: 'var(--bg-main)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '40px 16px'
    }}>
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

      <div className="card" style={{ maxWidth: '520px', width: '100%', padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <span className="badge badge-success" style={{ marginBottom: '8px' }}>14-Day Full Access Trial</span>
          <h2 style={{ fontSize: '24px', color: 'var(--secondary)', marginBottom: '6px' }}>Create Company Workspace</h2>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Each organization gets an isolated, secure database partition.
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
            marginBottom: '18px'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Company Name</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="Acme Technologies"
                value={formData.companyName}
                onChange={handleCompanyNameChange}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Workspace URL Slug</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="acme-tech"
                value={formData.slug}
                onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Admin First Name</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="Eleanor"
                value={formData.firstName}
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Admin Last Name</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="Pemberton"
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Corporate Email</label>
            <input
              type="email"
              required
              className="form-input"
              placeholder="eleanor@company.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Account Password</label>
            <input
              type="password"
              required
              minLength={8}
              className="form-input"
              placeholder="At least 8 characters"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />
          </div>

          <div style={{
            padding: '12px 14px',
            backgroundColor: 'var(--bg-main)',
            border: '1px solid var(--border)',
            borderRadius: 'var(--radius-md)',
            marginBottom: '20px',
            fontSize: '12px',
            color: 'var(--text-muted)'
          }}>
            <div style={{ fontWeight: 600, color: 'var(--secondary)', marginBottom: '4px' }}>
              Included in Your Workspace:
            </div>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: '3px' }}>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Check size={13} color="#16A36A" /> Free 14-day trial with full feature suite
              </li>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Check size={13} color="#16A36A" /> Unlimited visual workflows & AI assistant
              </li>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Check size={13} color="#16A36A" /> Automatic isolation & default departments
              </li>
            </ul>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ width: '100%' }}
          >
            {loading ? 'Setting Up Workspace...' : 'Create Company Workspace'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: 'var(--text-muted)' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ fontWeight: 600, color: 'var(--primary)' }}>
            Sign In
          </Link>
        </div>
      </div>
    </div>
  );
}
