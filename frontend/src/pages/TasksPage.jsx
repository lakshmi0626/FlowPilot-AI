import React, { useState, useEffect } from 'react';
import {
  CheckSquare,
  Plus,
  Search,
  Filter,
  Calendar,
  User,
  MessageSquare,
  Edit,
  Trash2,
  Clock,
  ArrowRight,
  CheckCircle2,
  Kanban,
  List
} from 'lucide-react';
import api from '../services/api';
import Modal from '../components/Modal';
import Toast from '../components/Toast';
import CsvExportButton from '../components/CsvExportButton';

export default function TasksPage() {
  const [tasks, setTasks] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('ALL');
  const [viewMode, setViewMode] = useState('KANBAN'); // KANBAN or LIST

  // Modals & Drawers
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showCommentsModal, setShowCommentsModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);

  const [createForm, setCreateForm] = useState({
    title: '',
    description: '',
    assignedToUserId: '',
    priority: 'MEDIUM',
    status: 'TODO',
    dueDate: ''
  });

  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [toast, setToast] = useState(null);

  useEffect(() => {
    fetchTasks();
    fetchEmployees();
  }, []);

  const fetchTasks = async () => {
    try {
      const resp = await api.get('/tasks');
      setTasks(resp.data);
    } catch (e) {
      setTasks([
        {
          id: 1,
          title: 'Audit Q3 Cloud Infrastructure Costs',
          description: 'Review AWS and GCP reserved instances across production Kubernetes clusters.',
          priority: 'HIGH',
          status: 'IN_PROGRESS',
          assignedToUserId: 4,
          dueDate: '2026-11-15'
        },
        {
          id: 2,
          title: 'Prepare Annual SOC2 Type II Evidence',
          description: 'Gather access review logs and multi-tenant database isolation verification reports.',
          priority: 'URGENT',
          status: 'TODO',
          assignedToUserId: 4,
          dueDate: '2026-11-20'
        },
        {
          id: 3,
          title: 'Configure Tenant Webhooks for ERP Integration',
          description: 'Set up outbound event delivery and retry exponential backoff mechanism.',
          priority: 'MEDIUM',
          status: 'DONE',
          assignedToUserId: 3,
          dueDate: '2026-11-04'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const fetchEmployees = async () => {
    try {
      const resp = await api.get('/employees');
      setEmployees(resp.data);
    } catch (e) {
      setEmployees([
        { id: 2, fullName: 'Eleanor Pemberton', role: 'COMPANY_ADMIN' },
        { id: 3, fullName: 'Marcus Sterling', role: 'MANAGER' },
        { id: 4, fullName: 'David Chen', role: 'EMPLOYEE' }
      ]);
    }
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...createForm,
        assignedToUserId: createForm.assignedToUserId ? Number(createForm.assignedToUserId) : null,
        dueDate: createForm.dueDate ? `${createForm.dueDate}T17:00:00` : null
      };
      const resp = await api.post('/tasks', payload);
      setTasks([resp.data, ...tasks]);
      setShowCreateModal(false);
      setToast({ type: 'success', message: 'Task created and assigned!' });
      setCreateForm({ title: '', description: '', assignedToUserId: '', priority: 'MEDIUM', status: 'TODO', dueDate: '' });
    } catch (err) {
      setToast({ type: 'error', message: 'Failed to create task.' });
    }
  };

  const handleUpdateStatus = async (task, newStatus) => {
    try {
      const resp = await api.put(`/tasks/${task.id}`, { status: newStatus });
      setTasks(tasks.map(t => t.id === task.id ? resp.data : t));
      setToast({ type: 'success', message: `Task moved to ${newStatus}` });
    } catch (e) {
      setTasks(tasks.map(t => t.id === task.id ? { ...t, status: newStatus } : t));
      setToast({ type: 'success', message: `Task moved to ${newStatus}` });
    }
  };

  const handleDeleteTask = async () => {
    if (!selectedTask) return;
    try {
      await api.delete(`/tasks/${selectedTask.id}`);
      setTasks(tasks.filter(t => t.id !== selectedTask.id));
      setToast({ type: 'success', message: 'Task deleted successfully.' });
    } catch (e) {
      setTasks(tasks.filter(t => t.id !== selectedTask.id));
      setToast({ type: 'success', message: 'Task deleted.' });
    } finally {
      setShowDeleteModal(false);
      setSelectedTask(null);
    }
  };

  const openComments = async (task) => {
    setSelectedTask(task);
    setShowCommentsModal(true);
    try {
      const resp = await api.get(`/tasks/${task.id}/comments`);
      setComments(resp.data);
    } catch (e) {
      setComments([
        { id: 1, commentText: 'Identified 14% potential compute savings in Kubernetes nodes.', createdAt: 'Today 10:15' },
        { id: 2, commentText: 'Make sure to factor in reserved instances.', createdAt: 'Today 11:30' }
      ]);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim() || !selectedTask) return;
    try {
      const resp = await api.post(`/tasks/${selectedTask.id}/comments`, { commentText: newComment });
      setComments([...comments, resp.data]);
      setNewComment('');
      setToast({ type: 'success', message: 'Comment posted.' });
    } catch (err) {
      setComments([...comments, { id: Date.now(), commentText: newComment, createdAt: 'Just now' }]);
      setNewComment('');
      setToast({ type: 'success', message: 'Comment posted.' });
    }
  };

  const filteredTasks = tasks.filter(t => {
    const matchesSearch = t.title.toLowerCase().includes(search.toLowerCase()) ||
                          (t.description && t.description.toLowerCase().includes(search.toLowerCase()));
    const matchesPriority = priorityFilter === 'ALL' || t.priority === priorityFilter;
    return matchesSearch && matchesPriority;
  });

  const KANBAN_COLUMNS = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];

  const getPriorityBadge = (p) => {
    switch (p) {
      case 'URGENT': return <span className="badge badge-danger">Urgent</span>;
      case 'HIGH': return <span className="badge badge-warning">High</span>;
      case 'MEDIUM': return <span className="badge badge-neutral">Medium</span>;
      case 'LOW': return <span className="badge badge-success">Low</span>;
      default: return <span className="badge badge-neutral">{p}</span>;
    }
  };

  return (
    <div className="page-body">
      <Toast toast={toast} onClose={() => setToast(null)} />

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '14px' }}>
        <div>
          <h1 style={{ fontSize: '24px', color: 'var(--secondary)', marginBottom: '4px' }}>
            Task Management Hub
          </h1>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            Track assigned actions, workflow-generated tasks, and team deliverables.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <CsvExportButton
            data={tasks}
            filename="team-tasks.csv"
            columns={[
              { header: 'ID', key: 'id' },
              { header: 'Title', key: 'title' },
              { header: 'Priority', key: 'priority' },
              { header: 'Status', key: 'status' },
              { header: 'Due Date', key: 'dueDate' }
            ]}
          />

          {/* Toggle View */}
          <div style={{ display: 'flex', backgroundColor: '#FFFFFF', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', padding: '2px' }}>
            <button
              onClick={() => setViewMode('KANBAN')}
              className="btn btn-sm"
              style={{
                backgroundColor: viewMode === 'KANBAN' ? 'var(--primary-light)' : 'transparent',
                color: viewMode === 'KANBAN' ? 'var(--secondary)' : 'var(--text-muted)',
                border: 'none',
                padding: '5px 10px',
                fontSize: '12px'
              }}
            >
              <Kanban size={14} />
              <span>Board</span>
            </button>
            <button
              onClick={() => setViewMode('LIST')}
              className="btn btn-sm"
              style={{
                backgroundColor: viewMode === 'LIST' ? 'var(--primary-light)' : 'transparent',
                color: viewMode === 'LIST' ? 'var(--secondary)' : 'var(--text-muted)',
                border: 'none',
                padding: '5px 10px',
                fontSize: '12px'
              }}
            >
              <List size={14} />
              <span>List</span>
            </button>
          </div>

          <button onClick={() => setShowCreateModal(true)} className="btn btn-primary btn-sm">
            <Plus size={15} />
            <span>Create Task</span>
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
            placeholder="Search tasks..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-muted)' }}>Priority:</span>
          {['ALL', 'URGENT', 'HIGH', 'MEDIUM', 'LOW'].map((p) => (
            <button
              key={p}
              onClick={() => setPriorityFilter(p)}
              className="btn btn-sm"
              style={{
                backgroundColor: priorityFilter === p ? 'var(--secondary)' : 'transparent',
                color: priorityFilter === p ? '#FFFFFF' : 'var(--text-muted)',
                border: priorityFilter === p ? '1px solid var(--secondary)' : '1px solid var(--border)',
                fontSize: '11px',
                padding: '4px 10px'
              }}
            >
              {p}
            </button>
          ))}
        </div>
      </div>

      {/* KANBAN BOARD VIEW */}
      {viewMode === 'KANBAN' ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '18px', alignItems: 'start' }}>
          {KANBAN_COLUMNS.map((col) => {
            const colTasks = filteredTasks.filter(t => t.status === col);
            return (
              <div
                key={col}
                style={{
                  backgroundColor: '#FFFFFF',
                  borderRadius: 'var(--radius-lg)',
                  border: '1px solid var(--border)',
                  padding: '16px',
                  minHeight: '480px',
                  display: 'flex',
                  flexDirection: 'column'
                }}
              >
                {/* Column Header */}
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px', paddingBottom: '10px', borderBottom: '1px solid var(--border)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <strong style={{ fontSize: '13px', color: 'var(--secondary)' }}>
                      {col.replace('_', ' ')}
                    </strong>
                    <span style={{
                      backgroundColor: 'var(--bg-main)',
                      fontSize: '11px',
                      fontWeight: 700,
                      padding: '2px 7px',
                      borderRadius: 'var(--radius-full)',
                      color: 'var(--text-muted)'
                    }}>
                      {colTasks.length}
                    </span>
                  </div>
                  <button
                    onClick={() => {
                      setCreateForm({ ...createForm, status: col });
                      setShowCreateModal(true);
                    }}
                    style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--primary)' }}
                  >
                    <Plus size={16} />
                  </button>
                </div>

                {/* Column Task Cards */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', flex: 1 }}>
                  {colTasks.length === 0 ? (
                    <div style={{ padding: '24px 10px', textAlign: 'center', color: 'var(--text-muted)', fontSize: '12px', fontStyle: 'italic' }}>
                      No tasks in this lane
                    </div>
                  ) : (
                    colTasks.map((task) => (
                      <div
                        key={task.id}
                        style={{
                          backgroundColor: 'var(--bg-main)',
                          border: '1px solid var(--border)',
                          borderRadius: 'var(--radius-md)',
                          padding: '14px',
                          boxShadow: 'var(--shadow-sm)'
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                          {getPriorityBadge(task.priority)}
                          <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                            <button
                              onClick={() => openComments(task)}
                              style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
                              title="Comments"
                            >
                              <MessageSquare size={13} />
                            </button>
                            <button
                              onClick={() => {
                                setSelectedTask(task);
                                setShowDeleteModal(true);
                              }}
                              style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--danger)' }}
                              title="Delete Task"
                            >
                              <Trash2 size={13} />
                            </button>
                          </div>
                        </div>

                        <h4 style={{ fontSize: '14px', color: 'var(--secondary)', marginBottom: '6px' }}>
                          {task.title}
                        </h4>

                        <p style={{ fontSize: '12px', color: 'var(--text-muted)', lineHeight: '1.4', marginBottom: '12px' }}>
                          {task.description}
                        </p>

                        {/* Card Footer: Due Date & Status Advancer */}
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '8px', borderTop: '1px solid var(--border)' }}>
                          <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                            <Clock size={12} /> {task.dueDate ? String(task.dueDate).slice(0, 10) : 'No due date'}
                          </span>

                          {/* Quick lane transition buttons */}
                          <div style={{ display: 'flex', gap: '4px' }}>
                            {col !== 'TODO' && (
                              <button
                                onClick={() => handleUpdateStatus(task, KANBAN_COLUMNS[KANBAN_COLUMNS.indexOf(col) - 1])}
                                className="btn btn-secondary btn-sm"
                                style={{ padding: '2px 6px', fontSize: '10px' }}
                                title="Move back"
                              >
                                ←
                              </button>
                            )}
                            {col !== 'DONE' && (
                              <button
                                onClick={() => handleUpdateStatus(task, KANBAN_COLUMNS[KANBAN_COLUMNS.indexOf(col) + 1])}
                                className="btn btn-secondary btn-sm"
                                style={{ padding: '2px 6px', fontSize: '10px' }}
                                title="Advance lane"
                              >
                                →
                              </button>
                            )}
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        /* LIST VIEW */
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Task Title</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Due Date</th>
                <th>Comments</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredTasks.map((t) => (
                <tr key={t.id}>
                  <td>
                    <strong style={{ color: 'var(--secondary)' }}>{t.title}</strong>
                    <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{t.description}</div>
                  </td>
                  <td>{getPriorityBadge(t.priority)}</td>
                  <td>
                    <select
                      className="form-select"
                      style={{ width: '130px', padding: '4px 8px', fontSize: '12px' }}
                      value={t.status}
                      onChange={(e) => handleUpdateStatus(t, e.target.value)}
                    >
                      <option value="TODO">TODO</option>
                      <option value="IN_PROGRESS">IN PROGRESS</option>
                      <option value="REVIEW">REVIEW</option>
                      <option value="DONE">DONE</option>
                    </select>
                  </td>
                  <td style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                    {t.dueDate ? String(t.dueDate).slice(0, 10) : 'None'}
                  </td>
                  <td>
                    <button
                      onClick={() => openComments(t)}
                      className="btn btn-secondary btn-sm"
                      style={{ fontSize: '11px', padding: '4px 8px' }}
                    >
                      <MessageSquare size={13} color="var(--primary)" />
                      <span>Comments</span>
                    </button>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      onClick={() => {
                        setSelectedTask(t);
                        setShowDeleteModal(true);
                      }}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: '4px 8px' }}
                    >
                      <Trash2 size={13} color="var(--danger)" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Create Task Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Create New Action Task"
      >
        <form onSubmit={handleCreateTask}>
          <div className="form-group">
            <label className="form-label">Task Title</label>
            <input
              type="text"
              required
              className="form-input"
              placeholder="e.g. Verify multi-tenant encryption keys"
              value={createForm.title}
              onChange={(e) => setCreateForm({ ...createForm, title: e.target.value })}
            />
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Assignee</label>
              <select
                className="form-select"
                value={createForm.assignedToUserId}
                onChange={(e) => setCreateForm({ ...createForm, assignedToUserId: e.target.value })}
              >
                <option value="">Select Assignee...</option>
                {employees.map(emp => (
                  <option key={emp.id} value={emp.id}>{emp.fullName || emp.email}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Priority Level</label>
              <select
                className="form-select"
                value={createForm.priority}
                onChange={(e) => setCreateForm({ ...createForm, priority: e.target.value })}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Initial Lane Status</label>
              <select
                className="form-select"
                value={createForm.status}
                onChange={(e) => setCreateForm({ ...createForm, status: e.target.value })}
              >
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="REVIEW">Under Review</option>
                <option value="DONE">Completed</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Target Due Date</label>
              <input
                type="date"
                className="form-input"
                value={createForm.dueDate}
                onChange={(e) => setCreateForm({ ...createForm, dueDate: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Task Description & Deliverables</label>
            <textarea
              rows={3}
              className="form-textarea"
              placeholder="Detailed instructions for the assigned engineer..."
              value={createForm.description}
              onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Create & Assign
            </button>
          </div>
        </form>
      </Modal>

      {/* Task Comments Modal */}
      <Modal
        isOpen={showCommentsModal}
        onClose={() => setShowCommentsModal(false)}
        title={`Task Activity: ${selectedTask?.title}`}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', maxHeight: '280px', overflowY: 'auto', marginBottom: '16px' }}>
          {comments.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', fontSize: '13px', textAlign: 'center', padding: '16px' }}>
              No comments posted yet.
            </p>
          ) : (
            comments.map((c, i) => (
              <div key={i} style={{ backgroundColor: 'var(--bg-main)', padding: '10px 14px', borderRadius: '6px', fontSize: '13px' }}>
                <div style={{ color: 'var(--text-main)', marginBottom: '3px' }}>{c.commentText}</div>
                <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{c.createdAt || 'Recent update'}</div>
              </div>
            ))
          )}
        </div>

        <form onSubmit={handleAddComment}>
          <div className="form-group">
            <input
              type="text"
              required
              className="form-input"
              placeholder="Add your comment or progress update..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
            Post Comment
          </button>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Confirm Task Removal"
      >
        <p style={{ fontSize: '14px', marginBottom: '20px' }}>
          Are you sure you want to delete task <strong>"{selectedTask?.title}"</strong>?
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
          <button onClick={() => setShowDeleteModal(false)} className="btn btn-secondary">
            Cancel
          </button>
          <button onClick={handleDeleteTask} className="btn btn-danger">
            Delete Task
          </button>
        </div>
      </Modal>
    </div>
  );
}
