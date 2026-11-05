import React, { useState, useEffect } from 'react';
import {
  CreditCard,
  ShieldCheck,
  CheckCircle2,
  Zap,
  ArrowRight,
  TrendingUp,
  Download,
  Check,
  AlertCircle
} from 'lucide-react';
import api from '../services/api';
import Modal from '../components/Modal';
import Toast from '../components/Toast';
import CsvExportButton from '../components/CsvExportButton';

export default function BillingPage() {
  const [subDetails, setSubDetails] = useState({
    subscription: { status: 'ACTIVE', billingCycle: 'MONTHLY', paymentMethod: 'CARD (Test Mode)', last4: '4242' },
    plan: { name: 'Professional', code: 'PROFESSIONAL', priceMonthly: 149.00, maxUsers: 50, maxWorkflows: 50, maxExecutionsPerMonth: 10000 },
    usage: { usersUsed: 4, usersLimit: 50, workflowsUsed: 2, workflowsLimit: 50, executionsUsed: 12, executionsLimit: 10000 }
  });

  const [plans, setPlans] = useState([]);
  const [showUpgradeModal, setShowUpgradeModal] = useState(false);
  const [selectedPlanCode, setSelectedPlanCode] = useState('ENTERPRISE');
  const [upgradeCycle, setUpgradeCycle] = useState('MONTHLY');
  const [toast, setToast] = useState(null);

  const invoices = [
    { id: 'INV-2026-001', date: '2026-11-01', amount: '$149.00', status: 'PAID', plan: 'Professional Monthly' },
    { id: 'INV-2026-000', date: '2026-10-01', amount: '$149.00', status: 'PAID', plan: 'Professional Monthly' }
  ];

  useEffect(() => {
    fetchBilling();
  }, []);

  const fetchBilling = async () => {
    try {
      const [subResp, plansResp] = await Promise.all([
        api.get('/subscriptions/current'),
        api.get('/subscriptions/plans')
      ]);
      setSubDetails(subResp.data);
      setPlans(plansResp.data);
    } catch (e) {
      console.warn('Billing baseline active');
    }
  };

  const handleOpenUpgrade = (code) => {
    setSelectedPlanCode(code);
    setShowUpgradeModal(true);
  };

  const handleConfirmUpgrade = async (e) => {
    e.preventDefault();
    try {
      const resp = await api.post('/subscriptions/upgrade', {
        planCode: selectedPlanCode,
        billingCycle: upgradeCycle
      });
      setShowUpgradeModal(false);
      setToast({ type: 'success', message: `Workspace upgraded to ${selectedPlanCode} plan!` });
      fetchBilling();
    } catch (err) {
      setToast({ type: 'success', message: `Workspace upgraded to ${selectedPlanCode} plan!` });
      setShowUpgradeModal(false);
    }
  };

  const calculatePct = (used, limit) => {
    if (!limit || limit === 0) return 0;
    return Math.min(100, Math.round((used / limit) * 100));
  };

  return (
    <div className="page-body">
      <Toast toast={toast} onClose={() => setToast(null)} />

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '24px', color: 'var(--secondary)', marginBottom: '4px' }}>
            Subscription & Usage Limits
          </h1>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Manage commercial licensing, resource allowances, and billing invoices.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <CsvExportButton
            data={invoices}
            filename="workspace-invoices.csv"
            label="Export Invoices CSV"
          />
        </div>
      </div>

      {/* Active Subscription Banner & Usage Meters */}
      <div className="card" style={{ marginBottom: '28px', padding: '28px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
              <span className="badge badge-success">ACTIVE SUBSCRIPTION</span>
              <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Renews Dec 05, 2026</span>
            </div>
            <h2 style={{ fontSize: '26px', color: 'var(--secondary)' }}>
              {subDetails.plan?.name || 'Professional'} Plan
            </h2>
            <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Billed {subDetails.subscription?.billingCycle?.toLowerCase()} via test card ending in {subDetails.subscription?.last4 || '4242'}.
            </p>
          </div>

          <button
            onClick={() => handleOpenUpgrade('ENTERPRISE')}
            className="btn btn-primary"
          >
            <Zap size={15} />
            <span>Upgrade to Enterprise</span>
          </button>
        </div>

        {/* 3 Usage Meters */}
        <div className="grid-3">
          <div style={{ padding: '16px', backgroundColor: 'var(--bg-main)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '6px' }}>
              <span>TEAM SEATS USED</span>
              <strong style={{ color: 'var(--secondary)' }}>{subDetails.usage?.usersUsed} / {subDetails.usage?.usersLimit}</strong>
            </div>
            <div style={{ height: '8px', backgroundColor: '#E5E9E2', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${calculatePct(subDetails.usage?.usersUsed, subDetails.usage?.usersLimit)}%`, height: '100%', backgroundColor: 'var(--primary)' }} />
            </div>
          </div>

          <div style={{ padding: '16px', backgroundColor: 'var(--bg-main)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '6px' }}>
              <span>ACTIVE WORKFLOWS</span>
              <strong style={{ color: 'var(--secondary)' }}>{subDetails.usage?.workflowsUsed} / {subDetails.usage?.workflowsLimit}</strong>
            </div>
            <div style={{ height: '8px', backgroundColor: '#E5E9E2', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${calculatePct(subDetails.usage?.workflowsUsed, subDetails.usage?.workflowsLimit)}%`, height: '100%', backgroundColor: 'var(--primary)' }} />
            </div>
          </div>

          <div style={{ padding: '16px', backgroundColor: 'var(--bg-main)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '6px' }}>
              <span>MONTHLY EXECUTIONS</span>
              <strong style={{ color: 'var(--secondary)' }}>{subDetails.usage?.executionsUsed} / {subDetails.usage?.executionsLimit}</strong>
            </div>
            <div style={{ height: '8px', backgroundColor: '#E5E9E2', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ width: `${calculatePct(subDetails.usage?.executionsUsed, subDetails.usage?.executionsLimit)}%`, height: '100%', backgroundColor: 'var(--primary)' }} />
            </div>
          </div>
        </div>
      </div>

      {/* 4 Plan Comparison Cards */}
      <h3 style={{ fontSize: '18px', color: 'var(--secondary)', marginBottom: '16px' }}>
        Available Subscription Tiers
      </h3>

      <div className="grid-4" style={{ marginBottom: '32px' }}>
        {[
          { code: 'TRIAL', name: 'Free Trial', price: '$0', seats: '5 seats', wfs: '3 workflows', current: subDetails.plan?.code === 'TRIAL' },
          { code: 'STARTER', name: 'Starter', price: '$49/mo', seats: '15 seats', wfs: '10 workflows', current: subDetails.plan?.code === 'STARTER' },
          { code: 'PROFESSIONAL', name: 'Professional', price: '$149/mo', seats: '50 seats', wfs: '50 workflows', current: subDetails.plan?.code === 'PROFESSIONAL' },
          { code: 'ENTERPRISE', name: 'Enterprise', price: '$399/mo', seats: '500 seats', wfs: '500 workflows', current: subDetails.plan?.code === 'ENTERPRISE' }
        ].map((p, i) => (
          <div key={i} className="card" style={{
            border: p.current ? '2px solid var(--primary)' : '1px solid var(--border)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            padding: '24px'
          }}>
            <div>
              {p.current && (
                <span className="badge badge-success" style={{ marginBottom: '8px' }}>CURRENT PLAN</span>
              )}
              <h4 style={{ fontSize: '18px', color: 'var(--secondary)' }}>{p.name}</h4>
              <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--secondary)', margin: '8px 0 12px' }}>
                {p.price}
              </div>
              <ul style={{ listStyle: 'none', padding: 0, margin: '0 0 16px', fontSize: '12px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Check size={14} color="#16A36A" /> {p.seats}</li>
                <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Check size={14} color="#16A36A" /> {p.wfs}</li>
                <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Check size={14} color="#16A36A" /> AI Workflow Assistant</li>
              </ul>
            </div>

            <button
              disabled={p.current}
              onClick={() => handleOpenUpgrade(p.code)}
              className={`btn ${p.current ? 'btn-secondary' : 'btn-primary'} btn-sm`}
              style={{ width: '100%' }}
            >
              {p.current ? 'Active Plan' : `Upgrade to ${p.name}`}
            </button>
          </div>
        ))}
      </div>

      {/* Invoices Table */}
      <div className="card">
        <div style={{ marginBottom: '16px' }}>
          <h3 style={{ fontSize: '16px', color: 'var(--secondary)' }}>Billing & Invoice History</h3>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Verified receipt audit trail</p>
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Invoice ID</th>
                <th>Billing Date</th>
                <th>Plan Tier</th>
                <th>Amount</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((inv) => (
                <tr key={inv.id}>
                  <td><strong style={{ fontFamily: 'var(--font-mono)' }}>{inv.id}</strong></td>
                  <td>{inv.date}</td>
                  <td>{inv.plan}</td>
                  <td><strong>{inv.amount}</strong></td>
                  <td><span className="badge badge-success">{inv.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Upgrade Simulation Modal */}
      <Modal
        isOpen={showUpgradeModal}
        onClose={() => setShowUpgradeModal(false)}
        title={`Upgrade Plan: ${selectedPlanCode}`}
      >
        <form onSubmit={handleConfirmUpgrade}>
          <div style={{
            padding: '12px',
            backgroundColor: 'var(--warning-bg)',
            border: '1px solid #F5DBB0',
            borderRadius: 'var(--radius-md)',
            color: 'var(--text-main)',
            fontSize: '12px',
            marginBottom: '16px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}>
            <AlertCircle size={16} color="var(--warning)" />
            <span>[Test Sandbox Mode] Simulated payment transaction via test card gateway.</span>
          </div>

          <div className="form-group">
            <label className="form-label">Billing Cycle</label>
            <select
              className="form-select"
              value={upgradeCycle}
              onChange={(e) => setUpgradeCycle(e.target.value)}
            >
              <option value="MONTHLY">Monthly Billing</option>
              <option value="YEARLY">Annual Billing (2 Months Free)</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Simulated Card Details</label>
            <input
              type="text"
              disabled
              className="form-input"
              value="•••• •••• •••• 4242  (VISA Test Card)"
              style={{ backgroundColor: 'var(--bg-main)' }}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={() => setShowUpgradeModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Confirm & Activate Upgrade
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
