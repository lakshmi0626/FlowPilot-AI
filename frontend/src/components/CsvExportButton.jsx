import React from 'react';
import { Download } from 'lucide-react';

export default function CsvExportButton({ data, filename = 'flowpilot-export.csv', columns, label = 'Export CSV' }) {
  const handleExport = () => {
    if (!data || data.length === 0) {
      alert('No data available to export.');
      return;
    }

    const headers = columns ? columns.map(c => c.header) : Object.keys(data[0]);
    const keys = columns ? columns.map(c => c.key) : Object.keys(data[0]);

    const csvRows = [];
    csvRows.push(headers.join(','));

    for (const row of data) {
      const values = keys.map(key => {
        let val = row[key];
        if (val === null || val === undefined) val = '';
        if (typeof val === 'object') val = JSON.stringify(val);
        const escaped = ('' + val).replace(/"/g, '""');
        return `"${escaped}"`;
      });
      csvRows.push(values.join(','));
    }

    const csvContent = 'data:text/csv;charset=utf-8,' + csvRows.join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <button
      onClick={handleExport}
      className="btn btn-secondary btn-sm"
      style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}
      title="Download real CSV spreadsheet file"
    >
      <Download size={14} color="#16A36A" />
      <span>{label}</span>
    </button>
  );
}
