import React from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export default function Toast({ toast, onClose }) {
  if (!toast) return null;

  const isSuccess = toast.type === 'success';
  const isError = toast.type === 'error';

  return (
    <div style={{
      position: 'fixed',
      bottom: '24px',
      right: '24px',
      backgroundColor: '#FFFFFF',
      borderRadius: 'var(--radius-md)',
      boxShadow: 'var(--shadow-xl)',
      border: `1px solid ${isSuccess ? 'var(--primary-border)' : isError ? '#F3C4C3' : 'var(--border)'}`,
      padding: '14px 18px',
      display: 'flex',
      alignItems: 'center',
      gap: '12px',
      zIndex: 2000,
      minWidth: '300px',
      maxWidth: '450px',
      animation: 'modalEnter 0.2s ease-out'
    }}>
      {isSuccess && <CheckCircle2 size={20} color="var(--primary)" />}
      {isError && <AlertCircle size={20} color="var(--danger)" />}
      {!isSuccess && !isError && <Info size={20} color="var(--secondary)" />}

      <div style={{ flex: 1 }}>
        <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-main)' }}>
          {toast.title || (isSuccess ? 'Success' : isError ? 'Error' : 'Notice')}
        </div>
        <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
          {toast.message}
        </div>
      </div>

      <button
        onClick={onClose}
        style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
      >
        <X size={15} />
      </button>
    </div>
  );
}
