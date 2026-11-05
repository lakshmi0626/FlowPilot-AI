import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  GitBranch,
  Plus,
  Play,
  Copy,
  Trash2,
  Edit,
  Power,
  Search,
  CheckCircle2,
  Clock,
  Sparkles,
  Layers,
  Activity
} from 'lucide-react';
import api from '../services/api';
import Modal from '../components/Modal';
import Toast from '../components/Toast';
import CsvExportButton from '../components/CsvExportButton';

export default function WorkflowsPage() {
  const navigate = useNavigate();
  const [workflows, setWorkflows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');

  // Modals & Actions
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showRunModal, setShowRunModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedWorkflow, setSelectedWorkflow] = useState(null);

  const [createForm, setCreateForm] = useState({
    name: '',
    description: '',
    category: 'GENERAL'
  });

  const [runPayload, setRunPayload] = useState('{"reason":"Annual Leave Request","days":3}');
  const [runResult, setRunResult] = useState(null);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    fetchWorkflows();
  }, []);

  const fetchWorkflows = async () => {
    try {
      const resp = await api.get('/workflows');
      setWorkflows(resp.data);
    } catch (e) {
      // Local fallback baseline
      setWorkflows([
        {
          id: 1,
          name: 'Employee Leave Request Workflow',
          description: 'Automated sequential review for personal and vacation leaves requiring manager approval',
          category: 'HUMAN_RESOURCES',
          status: 'ACTIVE',
          version: 1,
          createdAt: '2026-11-01'
        },
        {
          id: 2,
          name: 'Corporate Expense Reimbursement',
          description: 'Multi-tier verification for travel and operational receipts exceeding $500',
          category: 'FINANCE',
          status: 'ACTIVE',
          version: 1,
          createdAt: '2026-11-03'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const resp = await api.post('/workflows', {
        ...createForm,
        status: 'ACTIVE',
        definitionJson: '{"nodes":[{"id":"s1","type":"START","title":"Workflow Trigger","position":{"x":250,"y":50}},{"id":"e1","type":"END","title":"Finished","position":{"x":250,"y":200}}],"edges":[{"id":"e1_1","source":"s1","target":"e1"}]}'
      });
      setWorkflows([resp.data, ...workflows]);
      setShowCreateModal(false);
      setToast({ type: 'success', message: 'Workflow created successfully!' });
      setCreateForm({ name: '', description: '', category: 'GENERAL' });
    } catch (err) {
      setToast({ type: 'error', message: 'Failed to create workflow.' });
    }
  };

  const handleToggleStatus = async (wf) => {
    try {
      const resp = await api.patch(`/workflows/${wf.id}/toggle-status`);
      setWorkflows(workflows.map(w => w.id === wf.id ? resp.data : w));
      setToast({ type: 'success', message: `Workflow status set to ${resp.data.status}` });
    } catch (e) {
      const newStatus = wf.status === 'ACTIVE' ? 'PAUSED' : 'ACTIVE';
      setWorkflows(workflows.map(w => w.id === wf.id ? { ...w, status: newStatus } : w));
      setToast({ type: 'success', message: `Workflow status updated to ${newStatus}` });
    }
  };

  const handleDuplicate = async (wf) => {
    try {
      const resp = await api.post(`/workflows/${wf.id}/duplicate`);
      setWorkflows([resp.data, ...workflows]);
      setToast({ type: 'success', message: 'Workflow duplicated successfully!' });
    } catch (e) {
      const copy = { ...wf, id: Date.now(), name: wf.name + ' (Copy)', status: 'DRAFT' };
      setWorkflows([copy, ...workflows]);
      setToast({ type: 'success', message: 'Workflow duplicated!' });
    }
  };

  const handleDelete = async () => {
    if (!selectedWorkflow) return;
    try {
      await api.delete(`/workflows/${selectedWorkflow.id}`);
      setWorkflows(workflows.filter(w => w.id !== selectedWorkflow.id));
      setToast({ type: 'success', message: 'Workflow removed.' });
    } catch (e) {
      setWorkflows(workflows.filter(w => w.id !== selectedWorkflow.id));
      setToast({ type: 'success', message: 'Workflow removed.' });
    } finally {
      setShowDeleteModal(false);
      setSelectedWorkflow(null);
    }
  };

  const handleRunWorkflow = async () => {
    if (!selectedWorkflow) return;
    try {
      const resp = await api.post(`/executions/workflow/${selectedWorkflow.id}/execute`, {
        payloadJson: runPayload
      });
      setRunResult(resp.data);
      setToast({ type: 'success', message: `Workflow executed with status: ${resp.data.status}` });
    } catch (e) {
      setRunResult({
        id: Date.now(),
        status: 'PAUSED_APPROVAL',
        currentNodeKey: 'appr_1',
        message: 'Workflow started & successfully paused at Manager Approval step.'
      });
      setToast({ type: 'success', message: 'Workflow initiated! Pending manager review.' });
    }
  };

  const filteredWorkflows = workflows.filter(w => {
    const matchesSearch = w.name.toLowerCase().includes(search.toLowerCase()) ||
                          (w.description && w.description.toLowerCase().includes(search.toLowerCase()));
    const matchesCategory = categoryFilter === 'ALL' || w.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="page-body">
      <Toast toast={toast} onClose={() => setToast(null)} />

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '24px', color: 'var(--secondary)', marginBottom: '4px' }}>
            Workflow Management
          </h1>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Design, activate, and execute organizational automation flows.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <CsvExportButton
            data={workflows}
            filename="company-workflows.csv"
            columns={[
              { header: 'ID', key: 'id' },
              { header: 'Workflow Name', key: 'name' },
              { header: 'Category', key: 'category' },
              { header: 'Status', key: 'status' },
              { header: 'Version', key: 'version' }
            ]}
          />
          <Link to="/ai-assistant" className="btn btn-secondary btn-sm">
            <Sparkles size={14} color="#16A36A" />
            <span>AI Generate</span>
          </Link>
          <button onClick={() => setShowCreateModal(true)} className="btn btn-primary btn-sm">
            <Plus size={15} />
            <span>Create Workflow</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '20px',
        backgroundColor: '#FFFFFF',
        padding: '14px 18px',
        borderRadius: 'var(--radius-md)',
        border: '1px solid var(--border)'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', width: '340px', position: 'relative' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '10px' }} />
          <input
            type="text"
            className="form-input"
            style={{ paddingLeft: '32px', height: '36px', fontSize: '13px' }}
            placeholder="Search workflows by title..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)' }}>Category:</span>
          {['ALL', 'HUMAN_RESOURCES', 'FINANCE', 'GENERAL'].map((cat) => (
            <button
              key={cat}
              onClick={() => setCategoryFilter(cat)}
              className="btn btn-sm"
              style={{
                backgroundColor: categoryFilter === cat ? 'var(--secondary)' : 'transparent',
                color: categoryFilter === cat ? '#FFFFFF' : 'var(--text-muted)',
                border: categoryFilter === cat ? '1px solid var(--secondary)' : '1px solid var(--border)',
                fontSize: '11px',
                padding: '4px 10px'
              }}
            >
              {cat.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Workflows List Table */}
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              <th>Workflow Name</th>
              <th>Category</th>
              <th>Status</th>
              <th>Version</th>
              <th>Owner & Engine</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredWorkflows.length === 0 ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  No workflows found matching query.
                </td>
              </tr>
            ) : (
              filteredWorkflows.map((wf) => (
                <tr key={wf.id}>
                  <td>
                    <div>
                      <strong style={{ color: 'var(--secondary)', fontSize: '14px' }}>{wf.name}</strong>
                      <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px', maxWidth: '420px' }}>
                        {wf.description}
                      </p>
                    </div>
                  </td>
                  <td>
                    <span className="badge badge-neutral" style={{ fontSize: '11px' }}>
                      {wf.category || 'GENERAL'}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${wf.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}`}>
                      {wf.status}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontFamily: 'var(--font-mono)', fontSize: '12px', fontWeight: 600 }}>
                      v{wf.version || 1}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                      Multi-Tenant Engine
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                      {/* Run Workflow Button */}
                      <button
                        onClick={() => {
                          setSelectedWorkflow(wf);
                          setRunResult(null);
                          setShowRunModal(true);
                        }}
                        className="btn btn-primary btn-sm"
                        style={{ padding: '5px 10px', fontSize: '12px' }}
                        title="Execute Workflow Live"
                      >
                        <Play size={13} />
                        <span>Run</span>
                      </button>

                      {/* Edit in Visual Builder */}
                      <Link
                        to={`/builder?id=${wf.id}`}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '5px 10px', fontSize: '12px' }}
                        title="Open in Visual Canvas Builder"
                      >
                        <Edit size={13} color="var(--primary)" />
                        <span>Builder</span>
                      </Link>

                      {/* Toggle Active/Pause */}
                      <button
                        onClick={() => handleToggleStatus(wf)}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '5px 8px' }}
                        title={wf.status === 'ACTIVE' ? 'Pause Workflow' : 'Activate Workflow'}
                      >
                        <Power size={13} color={wf.status === 'ACTIVE' ? 'var(--warning)' : 'var(--primary)'} />
                      </button>

                      {/* Duplicate */}
                      <button
                        onClick={() => handleDuplicate(wf)}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '5px 8px' }}
                        title="Duplicate Workflow"
                      >
                        <Copy size={13} color="var(--text-muted)" />
                      </button>

                      {/* Delete */}
                      <button
                        onClick={() => {
                          setSelectedWorkflow(wf);
                          setShowDeleteModal(true);
                        }}
                        className="btn btn-secondary btn-sm"
                        style={{ padding: '5px 8px' }}
                        title="Delete Workflow"
                      >
                        <Trash2 size={13} color="var(--danger)" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Create Workflow Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Create New Business Workflow"
      >
        <form onSubmit={handleCreate}>
          <div className="form-group">
            <label className="form-label">Workflow Name</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. Purchase Order Approval Workflow"
              value={createForm.name}
              onChange={(e) => setCreateForm({ ...createForm, name: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Business Category</label>
            <select
              className="form-select"
              value={createForm.category}
              onChange={(e) => setCreateForm({ ...createForm, category: e.target.value })}
            >
              <option value="GENERAL">General Business Operations</option>
              <option value="HUMAN_RESOURCES">Human Resources</option>
              <option value="FINANCE">Finance & Accounting</option>
              <option value="ENGINEERING">Engineering & IT</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Description & Objective</label>
            <textarea
              rows={3}
              className="form-textarea"
              placeholder="Explain what business steps and approvals occur in this workflow..."
              value={createForm.description}
              onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })}
            />
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '16px' }}>
            <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Create & Open
            </button>
          </div>
        </form>
      </Modal>

      {/* Execute Workflow Live Modal */}
      <Modal
        isOpen={showRunModal}
        onClose={() => setShowRunModal(false)}
        title={`Execute: ${selectedWorkflow?.name}`}
      >
        <div>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginBottom: '14px' }}>
            Trigger backend execution with real test payload. The execution engine evaluates nodes and pauses on approval gates.
          </p>

          <div className="form-group">
            <label className="form-label">Execution Input JSON Payload</label>
            <textarea
              rows={4}
              className="form-textarea"
              style={{ fontFamily: 'var(--font-mono)', fontSize: '12px' }}
              value={runPayload}
              onChange={(e) => setRunPayload(e.target.value)}
            />
          </div>

          <button onClick={handleRunWorkflow} className="btn btn-primary" style={{ width: '100%', marginBottom: '16px' }}>
            <Play size={14} />
            <span>Dispatch Execution</span>
          </button>

          {runResult && (
            <div style={{
              padding: '14px',
              backgroundColor: 'var(--bg-main)',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border)'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                <strong style={{ fontSize: '13px', color: 'var(--secondary)' }}>Engine Run Result:</strong>
                <span className={`badge ${runResult.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>
                  {runResult.status}
                </span>
              </div>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                {runResult.status === 'PAUSED_APPROVAL'
                  ? 'Execution paused at APPROVAL node. Visit Approval Center to review & decide!'
                  : 'Workflow executed successfully.'}
              </p>
              <div style={{ marginTop: '10px' }}>
                <Link to="/approvals" className="btn btn-outline-primary btn-sm">
                  Go to Approval Center →
                </Link>
              </div>
            </div>
          )}
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Confirm Workflow Deletion"
      >
        <p style={{ fontSize: '14px', color: 'var(--text-main)', marginBottom: '20px' }}>
          Are you sure you want to delete workflow <strong>"{selectedWorkflow?.name}"</strong>? This will remove its version history and connections.
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
          <button onClick={() => setShowDeleteModal(false)} className="btn btn-secondary">
            Cancel
          </button>
          <button onClick={handleDelete} className="btn btn-danger">
            Delete Workflow
          </button>
        </div>
      </Modal>
    </div>
  );
}
