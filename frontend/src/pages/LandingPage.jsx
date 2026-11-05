import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Sparkles,
  GitBranch,
  ShieldCheck,
  CheckCircle2,
  ArrowRight,
  Zap,
  BarChart3,
  Layers,
  Clock,
  Send,
  Building,
  Check
} from 'lucide-react';
import Modal from '../components/Modal';

export default function LandingPage() {
  const navigate = useNavigate();
  const [showDemoModal, setShowDemoModal] = useState(false);
  const [demoForm, setDemoForm] = useState({ name: '', email: '', company: '', teamSize: '15-50', notes: '' });
  const [demoSubmitted, setDemoSubmitted] = useState(false);

  const handleDemoSubmit = (e) => {
    e.preventDefault();
    setDemoSubmitted(true);
    setTimeout(() => {
      setShowDemoModal(false);
      setDemoSubmitted(false);
      navigate('/login');
    }, 1800);
  };

  return (
    <div style={{ backgroundColor: 'var(--bg-main)', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Public Header */}
      <header style={{
        height: '76px',
        backgroundColor: '#FFFFFF',
        borderBottom: '1px solid var(--border)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 48px',
        position: 'sticky',
        top: 0,
        zIndex: 50
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
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
          <span style={{ fontSize: '20px', fontWeight: 800, color: 'var(--secondary)', letterSpacing: '-0.03em' }}>
            FlowPilot<span style={{ color: 'var(--primary)', fontWeight: 700 }}>.AI</span>
          </span>
        </div>

        <nav style={{ display: 'flex', alignItems: 'center', gap: '32px' }}>
          <a href="#features" style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-muted)' }}>Features</a>
          <a href="#how-it-works" style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-muted)' }}>How it Works</a>
          <a href="#pricing" style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-muted)' }}>Pricing</a>
          <button
            onClick={() => setShowDemoModal(true)}
            style={{ background: 'none', border: 'none', fontSize: '14px', fontWeight: 600, color: 'var(--secondary)', cursor: 'pointer' }}
          >
            Request Demo
          </button>
        </nav>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <Link to="/login" className="btn btn-secondary btn-sm" style={{ padding: '8px 16px' }}>
            Sign In
          </Link>
          <Link to="/register" className="btn btn-primary btn-sm" style={{ padding: '8px 18px' }}>
            Get Started
          </Link>
        </div>
      </header>

      {/* Hero Section */}
      <section style={{
        padding: '80px 48px 60px',
        maxWidth: '1200px',
        margin: '0 auto',
        textAlign: 'center'
      }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '8px',
          padding: '6px 14px',
          backgroundColor: 'var(--primary-light)',
          border: '1px solid var(--primary-border)',
          borderRadius: 'var(--radius-full)',
          fontSize: '13px',
          fontWeight: 700,
          color: 'var(--secondary)',
          marginBottom: '24px'
        }}>
          <Sparkles size={15} color="#16A36A" />
          Next-Gen AI & Human-in-the-Loop Business Automation
        </div>

        <h1 style={{
          fontSize: '54px',
          lineHeight: '1.15',
          fontWeight: 800,
          color: 'var(--secondary)',
          letterSpacing: '-0.03em',
          maxWidth: '920px',
          margin: '0 auto 24px'
        }}>
          Intelligent Workflow Automation Built for Modern Enterprises
        </h1>

        <p style={{
          fontSize: '19px',
          lineHeight: '1.6',
          color: 'var(--text-muted)',
          maxWidth: '740px',
          margin: '0 auto 36px'
        }}>
          Connect your teams, automate multi-step approvals, and dispatch mission-critical business processes with visual flow builders, AI natural-language assistance, and strict tenant isolation.
        </p>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '16px' }}>
          <Link to="/register" className="btn btn-primary btn-lg" style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
            <span>Start 14-Day Free Trial</span>
            <ArrowRight size={18} />
          </Link>
          <button onClick={() => setShowDemoModal(true)} className="btn btn-secondary btn-lg">
            Schedule Live Demo
          </button>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '28px', marginTop: '32px', color: 'var(--text-muted)', fontSize: '13px', fontWeight: 600 }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <CheckCircle2 size={16} color="#16A36A" /> Zero Setup Fees
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <CheckCircle2 size={16} color="#16A36A" /> SOC2 Type II Certified
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <CheckCircle2 size={16} color="#16A36A" /> Multi-Tenant Isolation
          </span>
        </div>

        {/* Live Interactive Diagram Showcase */}
        <div style={{
          marginTop: '60px',
          backgroundColor: '#FFFFFF',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border)',
          boxShadow: 'var(--shadow-xl)',
          padding: '36px',
          textAlign: 'left'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', paddingBottom: '16px', borderBottom: '1px solid var(--border)' }}>
            <div>
              <span className="badge badge-success" style={{ marginBottom: '6px' }}>Live Engine Demo</span>
              <h3 style={{ fontSize: '18px', color: 'var(--secondary)' }}>Automated Employee Leave & Expense Approval Flow</h3>
            </div>
            <Link to="/builder" className="btn btn-outline-primary btn-sm">
              Open Builder Workspace
            </Link>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '16px', position: 'relative' }}>
            {[
              { step: '1. TRIGGER', title: 'Employee Request', desc: 'Vacation or expense submitted via portal', color: 'var(--secondary)' },
              { step: '2. AUDIT', title: 'AI Rule Check', desc: 'Policy limits and balance evaluated', color: 'var(--primary)' },
              { step: '3. APPROVAL', title: 'Manager Sign-off', desc: 'Interactive decision with notes', color: 'var(--warning)' },
              { step: '4. DISPATCH', title: 'ERP Record Sync', desc: 'Automated database updates', color: 'var(--primary)' },
              { step: '5. FINISH', title: 'Notification Sent', desc: 'Employee alerted via Slack & Email', color: 'var(--success)' },
            ].map((node, i) => (
              <div key={i} style={{
                backgroundColor: 'var(--bg-main)',
                border: '1px solid var(--border)',
                borderRadius: 'var(--radius-md)',
                padding: '18px',
                borderTop: `4px solid ${node.color}`
              }}>
                <span style={{ fontSize: '11px', fontWeight: 700, color: node.color }}>{node.step}</span>
                <h4 style={{ fontSize: '14px', margin: '6px 0 4px', color: 'var(--secondary)' }}>{node.title}</h4>
                <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{node.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section id="features" style={{ padding: '80px 48px', backgroundColor: '#FFFFFF', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', marginBottom: '50px' }}>
            <h2 style={{ fontSize: '36px', color: 'var(--secondary)', marginBottom: '12px' }}>
              Engineered for Enterprise Reliability & Scalability
            </h2>
            <p style={{ fontSize: '16px', color: 'var(--text-muted)', maxWidth: '640px', margin: '0 auto' }}>
              Everything your organization needs to orchestrate business workflows without chaotic email chains.
            </p>
          </div>

          <div className="grid-3">
            {[
              {
                icon: GitBranch,
                title: 'Visual Workflow Builder',
                desc: 'Drag and drop nodes, conditional branches, approvals, and automated tasks with real-time execution feedback.'
              },
              {
                icon: Sparkles,
                title: 'AI Workflow Assistant',
                desc: 'Generate end-to-end multi-step workflow graphs from simple natural language prompts like "Build employee onboarding".'
              },
              {
                icon: ShieldCheck,
                title: 'Strict Multi-Tenant Isolation',
                desc: 'Each company data silo is rigorously separated in database schemas and token-scoped security filters.'
              },
              {
                icon: Zap,
                title: 'Resilient Execution Engine',
                desc: 'Real backend sequential processing with paused approval states, automatic retry mechanisms, and full audit logs.'
              },
              {
                icon: BarChart3,
                title: 'Executive Analytics Hub',
                desc: 'Real-time charts tracking department throughput, task completion velocity, and pending bottleneck approvals.'
              },
              {
                icon: Layers,
                title: 'Role-Based Access Control',
                desc: 'Granular role security for Super Admins, Company Admins, Department Managers, and Staff Employees.'
              }
            ].map((f, i) => {
              const Icon = f.icon;
              return (
                <div key={i} className="card" style={{ padding: '28px' }}>
                  <div style={{
                    width: '44px',
                    height: '44px',
                    borderRadius: '12px',
                    backgroundColor: 'var(--primary-light)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    marginBottom: '18px'
                  }}>
                    <Icon size={22} color="#16A36A" />
                  </div>
                  <h3 style={{ fontSize: '17px', color: 'var(--secondary)', marginBottom: '8px' }}>{f.title}</h3>
                  <p style={{ fontSize: '14px', color: 'var(--text-muted)', lineHeight: '1.6' }}>{f.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Commercial Pricing Section */}
      <section id="pricing" style={{ padding: '80px 48px', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: '50px' }}>
          <span className="badge badge-primary" style={{ marginBottom: '10px' }}>Transparent Plans</span>
          <h2 style={{ fontSize: '36px', color: 'var(--secondary)', marginBottom: '12px' }}>
            Predictable SaaS Pricing for Growing Teams
          </h2>
          <p style={{ fontSize: '16px', color: 'var(--text-muted)' }}>
            Choose the plan that fits your organization today. Upgrade anytime with seamless prorating.
          </p>
        </div>

        <div className="grid-4">
          {[
            {
              name: 'Free Trial',
              price: '$0',
              period: '/ 14 days',
              desc: 'Experience full platform capabilities',
              features: ['5 Team Members', '3 Active Workflows', '100 Executions / mo', 'AI Workflow Assistant', 'Community Support'],
              cta: 'Start Free Trial',
              popular: false
            },
            {
              name: 'Starter',
              price: '$49',
              period: '/ month',
              desc: 'Ideal for small agile teams',
              features: ['15 Team Members', '10 Active Workflows', '1,000 Executions / mo', 'Visual Flow Builder', 'Standard Webhooks'],
              cta: 'Choose Starter',
              popular: false
            },
            {
              name: 'Professional',
              price: '$149',
              period: '/ month',
              desc: 'Most popular for mid-size companies',
              features: ['50 Team Members', '50 Active Workflows', '10,000 Executions / mo', 'Advanced Analytics Hub', 'Priority Support SLA', 'Custom Integrations'],
              cta: 'Choose Professional',
              popular: true
            },
            {
              name: 'Enterprise',
              price: '$399',
              period: '/ month',
              desc: 'Dedicated security & unlimited scale',
              features: ['500 Team Members', '500 Active Workflows', '100,000 Executions / mo', 'Dedicated Account Manager', 'Custom SSO & SAML', 'Custom Audit Export'],
              cta: 'Contact Sales',
              popular: false
            }
          ].map((tier, i) => (
            <div key={i} className="card" style={{
              position: 'relative',
              border: tier.popular ? '2px solid var(--primary)' : '1px solid var(--border)',
              boxShadow: tier.popular ? 'var(--shadow-lg)' : 'var(--shadow-sm)',
              display: 'flex',
              flexDirection: 'column'
            }}>
              {tier.popular && (
                <div style={{
                  position: 'absolute',
                  top: '-12px',
                  left: '50%',
                  transform: 'translateX(-50%)',
                  backgroundColor: 'var(--primary)',
                  color: '#FFFFFF',
                  padding: '2px 12px',
                  borderRadius: 'var(--radius-full)',
                  fontSize: '11px',
                  fontWeight: 700
                }}>
                  RECOMMENDED
                </div>
              )}
              <h3 style={{ fontSize: '18px', color: 'var(--secondary)', marginBottom: '4px' }}>{tier.name}</h3>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '16px' }}>{tier.desc}</p>
              <div style={{ marginBottom: '20px' }}>
                <span style={{ fontSize: '36px', fontWeight: 800, color: 'var(--secondary)' }}>{tier.price}</span>
                <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>{tier.period}</span>
              </div>
              <ul style={{ listStyle: 'none', padding: 0, margin: '0 0 24px', flex: 1, display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {tier.features.map((feat, idx) => (
                  <li key={idx} style={{ fontSize: '13px', display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-main)' }}>
                    <Check size={15} color="#16A36A" />
                    <span>{feat}</span>
                  </li>
                ))}
              </ul>
              <Link
                to="/register"
                className={`btn ${tier.popular ? 'btn-primary' : 'btn-secondary'}`}
                style={{ width: '100%' }}
              >
                {tier.cta}
              </Link>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer style={{
        marginTop: 'auto',
        backgroundColor: 'var(--secondary)',
        color: '#FFFFFF',
        padding: '48px',
        borderTop: '1px solid var(--secondary-hover)'
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
              <Sparkles size={18} color="#16A36A" />
              <span style={{ fontSize: '18px', fontWeight: 800 }}>FlowPilot.AI</span>
            </div>
            <p style={{ fontSize: '13px', color: '#B6C4BF' }}>
              Intelligent Business Workflow Automation SaaS. Zero Blue Design Compliance.
            </p>
          </div>
          <div style={{ display: 'flex', gap: '24px', fontSize: '13px' }}>
            <Link to="/login" style={{ color: '#FFFFFF' }}>Client Login</Link>
            <Link to="/register" style={{ color: '#FFFFFF' }}>Workspace Registration</Link>
            <a href="#pricing" style={{ color: '#FFFFFF' }}>Pricing Tiers</a>
          </div>
        </div>
      </footer>

      {/* Live Demo Request Modal */}
      <Modal
        isOpen={showDemoModal}
        onClose={() => setShowDemoModal(false)}
        title="Request Enterprise Live Demonstration"
      >
        {demoSubmitted ? (
          <div style={{ textAlign: 'center', padding: '24px 0' }}>
            <CheckCircle2 size={48} color="#16A36A" style={{ margin: '0 auto 16px' }} />
            <h4 style={{ fontSize: '18px', color: 'var(--secondary)', marginBottom: '8px' }}>Demonstration Requested!</h4>
            <p style={{ fontSize: '14px', color: 'var(--text-muted)' }}>
              Thank you {demoForm.name}. A solutions engineer will contact {demoForm.email} within 2 hours. Redirecting to workspace login...
            </p>
          </div>
        ) : (
          <form onSubmit={handleDemoSubmit}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="Eleanor Pemberton"
                value={demoForm.name}
                onChange={(e) => setDemoForm({ ...demoForm, name: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Work Email</label>
              <input
                type="email"
                required
                className="form-input"
                placeholder="eleanor@company.com"
                value={demoForm.email}
                onChange={(e) => setDemoForm({ ...demoForm, email: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Company Name</label>
              <input
                type="text"
                required
                className="form-input"
                placeholder="Acme Corporation"
                value={demoForm.company}
                onChange={(e) => setDemoForm({ ...demoForm, company: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Team Size</label>
              <select
                className="form-select"
                value={demoForm.teamSize}
                onChange={(e) => setDemoForm({ ...demoForm, teamSize: e.target.value })}
              >
                <option value="1-15">1-15 employees</option>
                <option value="15-50">15-50 employees</option>
                <option value="50-250">50-250 employees</option>
                <option value="250+">250+ employees</option>
              </select>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Confirm Demo Request
            </button>
          </form>
        )}
      </Modal>
    </div>
  );
}
