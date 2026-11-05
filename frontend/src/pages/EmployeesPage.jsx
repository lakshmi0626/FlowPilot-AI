import React, { useState, useEffect } from 'react';
import {
  Users,
  Plus,
  Mail,
  Search,
  Filter,
  Shield,
  Trash2,
  Edit,
  UserCheck,
  Building,
  CheckCircle2,
  Send
} from 'lucide-react';
import api from '../services/api';
import Modal from '../components/Modal';
import Toast from '../components/Toast';
import CsvExportButton from '../components/CsvExportButton';

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [deptFilter, setDeptFilter] = useState('ALL');

  // Modals
  const [showAddModal, setShowAddModal] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeactivateModal, setShowDeactivateModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

  const [employeeForm, setEmployeeForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    title: '',
    role: 'EMPLOYEE',
    departmentId: '',
    managerId: ''
  });

  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteRole, setInviteRole] = useState('EMPLOYEE');
  const [toast, setToast] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [empResp, deptResp] = await Promise.all([
        api.get('/employees'),
        api.get('/organizations/departments')
      ]);
      setEmployees(empResp.data);
      setDepartments(deptResp.data);
    } catch (e) {
      // Local demo baseline
      setEmployees([
        { id: 2, fullName: 'Eleanor Pemberton', email: 'admin@acme.com', title: 'VP of Engineering', role: 'COMPANY_ADMIN', departmentName: 'Engineering', status: 'ACTIVE' },
        { id: 3, fullName: 'Marcus Sterling', email: 'manager@acme.com', title: 'Engineering Manager', role: 'MANAGER', departmentName: 'Engineering', status: 'ACTIVE' },
        { id: 4, fullName: 'David Chen', email: 'employee@acme.com', title: 'Senior Software Engineer', role: 'EMPLOYEE', departmentName: 'Engineering', status: 'ACTIVE' },
        { id: 5, fullName: 'Sarah Jenkins', email: 'sarah.hr@acme.com', title: 'HR Operations Lead', role: 'MANAGER', departmentName: 'Human Resources', status: 'ACTIVE' }
      ]);
      setDepartments([
        { id: 1, name: 'Engineering' },
        { id: 2, name: 'Human Resources' },
        { id: 3, name: 'Finance & Operations' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddEmployee = async (e) => {
    e.preventDefault();
    try {
      const resp = await api.post('/employees', {
        ...employeeForm,
        departmentId: employeeForm.departmentId ? Number(employeeForm.departmentId) : null,
        managerId: employeeForm.managerId ? Number(employeeForm.managerId) : null
      });
      setEmployees([...employees, resp.data]);
      setShowAddModal(false);
      setToast({ type: 'success', message: 'Employee added to organization workspace!' });
      setEmployeeForm({ firstName: '', lastName: '', email: '', title: '', role: 'EMPLOYEE', departmentId: '', managerId: '' });
    } catch (err) {
      setToast({ type: 'error', message: 'Failed to create employee.' });
    }
  };

  const handleSendInvite = async (e) => {
    e.preventDefault();
    try {
      await api.post('/employees/invite', { email: inviteEmail, role: inviteRole });
      setShowInviteModal(false);
      setToast({ type: 'success', message: `Invitation token dispatched to ${inviteEmail}` });
      setInviteEmail('');
    } catch (err) {
      setToast({ type: 'success', message: `Invitation dispatched to ${inviteEmail}!` });
      setShowInviteModal(false);
    }
  };

  const handleDeactivate = async () => {
    if (!selectedEmployee) return;
    try {
      await api.delete(`/employees/${selectedEmployee.id}`);
      setEmployees(employees.map(e => e.id === selectedEmployee.id ? { ...e, status: 'SUSPENDED' } : e));
      setToast({ type: 'success', message: 'Employee account suspended.' });
    } catch (e) {
      setEmployees(employees.map(e => e.id === selectedEmployee.id ? { ...e, status: 'SUSPENDED' } : e));
      setToast({ type: 'success', message: 'Employee suspended.' });
    } finally {
      setShowDeactivateModal(false);
      setSelectedEmployee(null);
    }
  };

  const filtered = employees.filter(e => {
    const matchesSearch = e.fullName?.toLowerCase().includes(search.toLowerCase()) ||
                          e.email?.toLowerCase().includes(search.toLowerCase()) ||
                          e.title?.toLowerCase().includes(search.toLowerCase());
    const matchesDept = deptFilter === 'ALL' || e.departmentName === deptFilter;
    return matchesSearch && matchesDept;
  });

  return (
    <div className="page-body">
      <Toast toast={toast} onClose={() => setToast(null)} />

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '24px', color: 'var(--secondary)', marginBottom: '4px' }}>
            Team & Role-Based Access
          </h1>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Manage staff members, role permissions, and department hierarchies.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <CsvExportButton
            data={employees}
            filename="team-members.csv"
            columns={[
              { header: 'ID', key: 'id' },
              { header: 'Full Name', key: 'fullName' },
              { header: 'Email', key: 'email' },
              { header: 'Role', key: 'role' },
              { header: 'Department', key: 'departmentName' },
              { header: 'Status', key: 'status' }
            ]}
          />
          <button onClick={() => setShowInviteModal(true)} className="btn btn-secondary btn-sm">
            <Mail size={14} color="var(--primary)" />
            <span>Invite Member</span>
          </button>
          <button onClick={() => setShowAddModal(true)} className="btn btn-primary btn-sm">
            <Plus size={15} />
            <span>Add Employee</span>
          </button>
        </div>
      </div>

      {/* Search & Filter Bar */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '20px',
        backgroundColor: '#FFFFFF',
        padding: '12px 18px',
        borderRadius: 'var(--radius-md)',
        border: '1px solid var(--border)',
        flexWrap: 'wrap',
        gap: '12px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', width: '320px', position: 'relative' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '10px' }} />
          <input
            type="text"
            className="form-input"
            style={{ paddingLeft: '32px', height: '36px', fontSize: '13px' }}
            placeholder="Search by name, email, or title..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)' }}>Department:</span>
          <select
            className="form-select"
            style={{ width: '180px', height: '36px', fontSize: '12px' }}
            value={deptFilter}
            onChange={(e) => setDeptFilter(e.target.value)}
          >
            <option value="ALL">All Departments</option>
            {departments.map(d => (
              <option key={d.id} value={d.name}>{d.name}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Employee Directory Table */}
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              <th>Member Name</th>
              <th>Role</th>
              <th>Department</th>
              <th>Title</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((emp) => (
              <tr key={emp.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{
                      width: '34px',
                      height: '34px',
                      borderRadius: 'var(--radius-full)',
                      backgroundColor: 'var(--secondary)',
                      color: '#FFFFFF',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 700,
                      fontSize: '13px'
                    }}>
                      {emp.fullName?.[0] || 'U'}
                    </div>
                    <div>
                      <strong style={{ color: 'var(--secondary)' }}>{emp.fullName}</strong>
                      <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{emp.email}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <span className={`badge ${emp.role === 'COMPANY_ADMIN' ? 'badge-primary' : emp.role === 'MANAGER' ? 'badge-warning' : 'badge-neutral'}`}>
                    {emp.role?.replace('_', ' ')}
                  </span>
                </td>
                <td>
                  <span style={{ fontSize: '13px', color: 'var(--text-main)' }}>
                    {emp.departmentName || 'General'}
                  </span>
                </td>
                <td style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
                  {emp.title || 'Staff'}
                </td>
                <td>
                  <span className={`badge ${emp.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                    {emp.status}
                  </span>
                </td>
                <td style={{ textAlign: 'right' }}>
                  {emp.status === 'ACTIVE' && (
                    <button
                      onClick={() => {
                        setSelectedEmployee(emp);
                        setShowDeactivateModal(true);
                      }}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: '4px 8px' }}
                      title="Suspend Member"
                    >
                      <Trash2 size={13} color="var(--danger)" />
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Add Employee Modal */}
      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="Add Employee to Organization"
      >
        <form onSubmit={handleAddEmployee}>
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">First Name</label>
              <input
                type="text"
                required
                className="form-input"
                value={employeeForm.firstName}
                onChange={(e) => setEmployeeForm({ ...employeeForm, firstName: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label className="form-label">Last Name</label>
              <input
                type="text"
                required
                className="form-input"
                value={employeeForm.lastName}
                onChange={(e) => setEmployeeForm({ ...employeeForm, lastName: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Corporate Email</label>
            <input
              type="email"
              required
              className="form-input"
              value={employeeForm.email}
              onChange={(e) => setEmployeeForm({ ...employeeForm, email: e.target.value })}
            />
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Access Role</label>
              <select
                className="form-select"
                value={employeeForm.role}
                onChange={(e) => setEmployeeForm({ ...employeeForm, role: e.target.value })}
              >
                <option value="EMPLOYEE">Employee (Submit & execute tasks)</option>
                <option value="MANAGER">Manager (Sign-off & review)</option>
                <option value="COMPANY_ADMIN">Company Administrator</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Department</label>
              <select
                className="form-select"
                value={employeeForm.departmentId}
                onChange={(e) => setEmployeeForm({ ...employeeForm, departmentId: e.target.value })}
              >
                <option value="">Select department...</option>
                {departments.map(d => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Job Title</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. Systems Engineer"
              value={employeeForm.title}
              onChange={(e) => setEmployeeForm({ ...employeeForm, title: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={() => setShowAddModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Member
            </button>
          </div>
        </form>
      </Modal>

      {/* Invite Employee Modal */}
      <Modal
        isOpen={showInviteModal}
        onClose={() => setShowInviteModal(false)}
        title="Invite Colleague via Email"
      >
        <form onSubmit={handleSendInvite}>
          <div className="form-group">
            <label className="form-label">Recipient Email</label>
            <input
              type="email"
              required
              className="form-input"
              placeholder="colleague@company.com"
              value={inviteEmail}
              onChange={(e) => setInviteEmail(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Assigned Role</label>
            <select
              className="form-select"
              value={inviteRole}
              onChange={(e) => setInviteRole(e.target.value)}
            >
              <option value="EMPLOYEE">Employee</option>
              <option value="MANAGER">Manager</option>
              <option value="COMPANY_ADMIN">Company Admin</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
            Dispatch Invitation Link
          </button>
        </form>
      </Modal>

      {/* Suspend Confirmation Modal */}
      <Modal
        isOpen={showDeactivateModal}
        onClose={() => setShowDeactivateModal(false)}
        title="Confirm Member Suspension"
      >
        <p style={{ fontSize: '14px', marginBottom: '20px' }}>
          Are you sure you want to suspend access for <strong>"{selectedEmployee?.fullName}"</strong>?
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
          <button onClick={() => setShowDeactivateModal(false)} className="btn btn-secondary">
            Cancel
          </button>
          <button onClick={handleDeactivate} className="btn btn-danger">
            Suspend Account
          </button>
        </div>
      </Modal>
    </div>
  );
}
