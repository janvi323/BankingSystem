import React, { useState, useEffect } from 'react';
import { AlertCircle, CheckCircle, Clock, TrendingDown, Loader } from 'lucide-react';
import './EMIPayment.css';

// ============================================
// React EMI Payment Component
// This replaces the 600+ line JSP file
// ============================================

interface EMI {
  id: number;
  emiNumber: number;
  dueDate: string;
  amount: number;
  status: 'PENDING' | 'PAID' | 'OVERDUE';
  paymentDate?: string;
  paymentMethod?: string;
  loan: {
    id: number;
    purpose: string;
    tenure: number;
  };
}

interface EMIStats {
  totalEMIs: number;
  pendingEMIs: number;
  overdueEMIs: number;
  paidEMIs: number;
  totalPendingAmount: number;
  totalOverdueAmount: number;
}

// API Client with credentials
const api = {
  getStats: () => fetch('/api/emi/stats', {
    credentials: 'include',
    headers: { 'X-Requested-With': 'XMLHttpRequest' }
  }).then(r => r.json()),

  getMyEMIs: () => fetch('/api/emi/my-emis', {
    credentials: 'include',
    headers: { 'X-Requested-With': 'XMLHttpRequest' }
  }).then(r => r.json()),

  getDueThisMonth: () => fetch('/api/emi/due-this-month', {
    credentials: 'include',
    headers: { 'X-Requested-With': 'XMLHttpRequest' }
  }).then(r => r.json()),

  getOverdue: () => fetch('/api/emi/overdue', {
    credentials: 'include',
    headers: { 'X-Requested-With': 'XMLHttpRequest' }
  }).then(r => r.json()),

  payEMI: (emiId: number, paymentMethod: string) => fetch(`/api/emi/pay/${emiId}`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest'
    },
    body: JSON.stringify({ paymentMethod })
  }).then(async r => {
    if (!r.ok) throw new Error(await r.text());
    return r.text();
  })
};

// ============================================
// Stats Card Component
// ============================================
const StatsCard: React.FC<{
  title: string;
  value: string | number;
  icon: React.ReactNode;
  variant: 'default' | 'warning' | 'success' | 'danger';
}> = ({ title, value, icon, variant }) => {
  const variantClass = `stats-card stats-card-${variant}`;
  return (
    <div className={variantClass}>
      <div className="stats-icon">{icon}</div>
      <div className="stats-content">
        <p className="stats-label">{title}</p>
        <p className="stats-value">{value}</p>
      </div>
    </div>
  );
};

// ============================================
// EMI Table Component
// ============================================
const EMITable: React.FC<{
  emis: EMI[];
  showPayButton?: boolean;
  onPay?: (emi: EMI) => void;
}> = ({ emis, showPayButton = true, onPay }) => {
  const formatCurrency = (amount: number) => 
    `₹${amount.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;

  const formatDate = (date: string) => 
    new Date(date).toLocaleDateString('en-IN');

  const getStatusBadge = (status: string, dueDate: string) => {
    if (status === 'PAID') {
      return <span className="badge badge-success">✓ Paid</span>;
    }
    if (status === 'OVERDUE') {
      return <span className="badge badge-danger">⚠ Overdue</span>;
    }
    return <span className="badge badge-warning">⏱ Pending</span>;
  };

  if (emis.length === 0) {
    return <p className="empty-state">No EMIs found.</p>;
  }

  return (
    <div className="table-container">
      <table className="emi-table">
        <thead>
          <tr>
            <th>EMI #</th>
            <th>Loan Purpose</th>
            <th>Due Date</th>
            <th>Amount</th>
            <th>Status</th>
            {showPayButton && <th>Action</th>}
          </tr>
        </thead>
        <tbody>
          {emis.map(emi => (
            <tr key={emi.id}>
              <td>{emi.emiNumber}/{emi.loan.tenure}</td>
              <td>{emi.loan.purpose}</td>
              <td>{formatDate(emi.dueDate)}</td>
              <td className="amount">{formatCurrency(emi.amount)}</td>
              <td>{getStatusBadge(emi.status, emi.dueDate)}</td>
              {showPayButton && (
                <td>
                  {emi.status === 'PENDING' ? (
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => onPay?.(emi)}
                    >
                      Pay Now
                    </button>
                  ) : (
                    <span className="text-muted">-</span>
                  )}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

// ============================================
// Payment Modal Component
// ============================================
const PaymentModal: React.FC<{
  emi: EMI | null;
  isOpen: boolean;
  isLoading: boolean;
  onClose: () => void;
  onPay: (paymentMethod: string) => void;
}> = ({ emi, isOpen, isLoading, onClose, onPay }) => {
  const [paymentMethod, setPaymentMethod] = useState('Online Banking');

  if (!isOpen || !emi) return null;

  const formatCurrency = (amount: number) =>
    `₹${amount.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Confirm Payment</h3>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        
        <div className="modal-body">
          <div className="payment-details">
            <p><strong>Amount:</strong> {formatCurrency(emi.amount)}</p>
            <p><strong>Loan:</strong> {emi.loan.purpose}</p>
            <p><strong>EMI:</strong> {emi.emiNumber}/{emi.loan.tenure}</p>
            <p><strong>Due Date:</strong> {new Date(emi.dueDate).toLocaleDateString('en-IN')}</p>
          </div>

          <div className="form-group">
            <label htmlFor="paymentMethod">Payment Method:</label>
            <select
              id="paymentMethod"
              value={paymentMethod}
              onChange={e => setPaymentMethod(e.target.value)}
              disabled={isLoading}
            >
              <option>Online Banking</option>
              <option>Credit Card</option>
              <option>Debit Card</option>
              <option>UPI</option>
              <option>Wallet</option>
            </select>
          </div>
        </div>

        <div className="modal-footer">
          <button
            className="btn btn-secondary"
            onClick={onClose}
            disabled={isLoading}
          >
            Cancel
          </button>
          <button
            className="btn btn-primary"
            onClick={() => onPay(paymentMethod)}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <Loader className="spinner" />
                Processing...
              </>
            ) : (
              'Pay Now'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

// ============================================
// Alert Component
// ============================================
const Alert: React.FC<{
  type: 'success' | 'danger' | 'warning' | 'info';
  message: string;
  onClose: () => void;
}> = ({ type, message, onClose }) => {
  useEffect(() => {
    const timer = setTimeout(onClose, 5000);
    return () => clearTimeout(timer);
  }, [onClose]);

  const iconMap = {
    success: <CheckCircle />,
    danger: <AlertCircle />,
    warning: <Clock />,
    info: <AlertCircle />
  };

  return (
    <div className={`alert alert-${type}`}>
      <div className="alert-icon">{iconMap[type]}</div>
      <div className="alert-content">{message}</div>
      <button className="alert-close" onClick={onClose}>✕</button>
    </div>
  );
};

// ============================================
// Main EMI Payment Page Component
// ============================================
export const EMIPaymentPage: React.FC = () => {
  const [stats, setStats] = useState<EMIStats | null>(null);
  const [allEMIs, setAllEMIs] = useState<EMI[]>([]);
  const [thisMonthEMIs, setThisMonthEMIs] = useState<EMI[]>([]);
  const [overdueEMIs, setOverdueEMIs] = useState<EMI[]>([]);
  const [loading, setLoading] = useState(true);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [selectedEMI, setSelectedEMI] = useState<EMI | null>(null);
  const [alert, setAlert] = useState<{
    type: 'success' | 'danger' | 'warning' | 'info';
    message: string;
  } | null>(null);

  // Load EMI data on mount
  useEffect(() => {
    loadEMIData();
  }, []);

  const loadEMIData = async () => {
    setLoading(true);
    try {
      const [statsData, allData, monthData, overdueData] = await Promise.all([
        api.getStats(),
        api.getMyEMIs(),
        api.getDueThisMonth(),
        api.getOverdue()
      ]);

      setStats(statsData);
      setAllEMIs(allData);
      setThisMonthEMIs(monthData);
      setOverdueEMIs(overdueData);
    } catch (error) {
      setAlert({
        type: 'danger',
        message: `Error loading EMI data: ${error instanceof Error ? error.message : 'Unknown error'}`
      });
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (paymentMethod: string) => {
    if (!selectedEMI) return;

    setPaymentLoading(true);
    try {
      await api.payEMI(selectedEMI.id, paymentMethod);
      setAlert({
        type: 'success',
        message: `✅ EMI payment successful! Amount: ₹${selectedEMI.amount.toLocaleString('en-IN')} paid via ${paymentMethod}`
      });
      setSelectedEMI(null);
      // Reload EMI data after 1 second to allow DB write
      setTimeout(loadEMIData, 1000);
    } catch (error) {
      setAlert({
        type: 'danger',
        message: `❌ Payment Error: ${error instanceof Error ? error.message : 'Payment failed'}`
      });
    } finally {
      setPaymentLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-state">
          <Loader className="spinner" />
          <p>Loading EMI data...</p>
        </div>
      </div>
    );
  }

  const formatCurrency = (amount: number) =>
    `₹${amount.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;

  return (
    <div className="page-container">
      {/* Alerts */}
      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {/* Header */}
      <div className="page-header">
        <h1>📊 My EMI Payments</h1>
        <p>Manage and pay your loan EMIs</p>
      </div>

      {/* Stats Section */}
      {stats && (
        <div className="stats-grid">
          <StatsCard
            title="Total EMIs"
            value={stats.totalEMIs}
            icon={<Clock />}
            variant="default"
          />
          <StatsCard
            title="Pending"
            value={stats.pendingEMIs}
            icon={<Clock />}
            variant="warning"
          />
          <StatsCard
            title="Overdue"
            value={stats.overdueEMIs}
            icon={<AlertCircle />}
            variant="danger"
          />
          <StatsCard
            title="Paid"
            value={stats.paidEMIs}
            icon={<CheckCircle />}
            variant="success"
          />
          <StatsCard
            title="Total Pending"
            value={formatCurrency(stats.totalPendingAmount)}
            icon={<TrendingDown />}
            variant="warning"
          />
          <StatsCard
            title="Total Overdue"
            value={formatCurrency(stats.totalOverdueAmount)}
            icon={<AlertCircle />}
            variant="danger"
          />
        </div>
      )}

      {/* This Month Section */}
      {thisMonthEMIs.length > 0 && (
        <section className="section">
          <h2>📅 Due This Month</h2>
          <EMITable
            emis={thisMonthEMIs}
            onPay={setSelectedEMI}
          />
        </section>
      )}

      {/* Overdue Section */}
      {overdueEMIs.length > 0 && (
        <section className="section section-alert">
          <h2>⚠️ Overdue EMIs</h2>
          <EMITable
            emis={overdueEMIs}
            onPay={setSelectedEMI}
          />
        </section>
      )}

      {/* All EMIs Section */}
      <section className="section">
        <h2>📋 All EMIs</h2>
        <EMITable
          emis={allEMIs}
          onPay={setSelectedEMI}
        />
      </section>

      {/* Payment Modal */}
      <PaymentModal
        emi={selectedEMI}
        isOpen={selectedEMI !== null}
        isLoading={paymentLoading}
        onClose={() => setSelectedEMI(null)}
        onPay={handlePayment}
      />
    </div>
  );
};

export default EMIPaymentPage;
