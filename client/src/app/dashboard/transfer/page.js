'use client';

import { useEffect, useState, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { toast } from 'sonner';
import { accounts, transactions } from '@/lib/api';

function TransferForm() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [accountList, setAccountList] = useState([]);
  const [form, setForm] = useState({
    fromAccount: searchParams.get('from') || '',
    toAccount: '',
    amount: '',
  });
  const [loading, setLoading] = useState(false);
  const [loadingAccounts, setLoadingAccounts] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      router.push('/login');
      return;
    }
    accounts.list()
      .then((data) => setAccountList(data))
      .catch(() => router.push('/login'))
      .finally(() => setLoadingAccounts(false));
  }, [router]);

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.fromAccount === form.toAccount) {
      toast.error('Source and destination accounts must be different.');
      return;
    }
    if (!form.amount || parseFloat(form.amount) <= 0) {
      toast.error('Amount must be greater than 0.');
      return;
    }

    setLoading(true);
    const toastId = toast.loading('Processing transfer…');
    try {
      const result = await transactions.create({
        fromAccount: form.fromAccount,
        toAccount: form.toAccount,
        amount: parseFloat(form.amount),
        idempotencyKey: crypto.randomUUID(),
      });
      toast.success(`Transfer successful! TX: ${result.transaction.id}`, { id: toastId });
      setForm((prev) => ({ ...prev, toAccount: '', amount: '' }));
    } catch (err) {
      toast.error(err.message, { id: toastId });
    } finally {
      setLoading(false);
    }
  };

  const activeAccounts = accountList.filter((a) => a.status === 'ACTIVE');

  return (
    <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl p-8 shadow-sm">
      <h1 className="text-2xl font-bold text-slate-900 mb-1">Send Money</h1>
      <p className="text-sm text-slate-500 mb-6">Transfer funds between accounts</p>

      {loadingAccounts ? (
        <div className="flex items-center justify-center py-12 text-slate-400 text-sm">Loading accounts…</div>
      ) : activeAccounts.length < 2 ? (
        <div className="text-center py-8">
          <p className="text-slate-500 text-sm mb-3">You need at least 2 active accounts to transfer.</p>
          <Link href="/dashboard" className="text-blue-600 hover:underline text-sm">Go to dashboard</Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">From account</label>
            <select
              name="fromAccount"
              value={form.fromAccount}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option value="">Select source account</option>
              {activeAccounts.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.id.slice(0, 20)}… ({a.currency})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">To account</label>
            <input
              type="text"
              name="toAccount"
              value={form.toAccount}
              onChange={handleChange}
              required
              placeholder="Destination account ID"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono"
            />
            {activeAccounts.length > 0 && (
              <div className="mt-2 flex flex-col gap-1">
                <p className="text-xs text-slate-400">Your active accounts (click to fill):</p>
                {activeAccounts
                  .filter((a) => a.id !== form.fromAccount)
                  .map((a) => (
                    <button
                      key={a.id}
                      type="button"
                      onClick={() => setForm((prev) => ({ ...prev, toAccount: a.id }))}
                      className="text-left text-xs font-mono px-2 py-1 bg-slate-50 hover:bg-blue-50 hover:text-blue-700 rounded border border-slate-200 truncate transition-colors"
                    >
                      {a.id}
                    </button>
                  ))}
              </div>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Amount (₹)</label>
            <input
              type="number"
              name="amount"
              value={form.amount}
              onChange={handleChange}
              required
              min="0.01"
              step="0.01"
              placeholder="0.00"
              className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm"
          >
            {loading ? 'Processing…' : 'Send money'}
          </button>
        </form>
      )}

      <div className="mt-6 text-center">
        <Link href="/dashboard" className="text-sm text-slate-500 hover:text-blue-600 transition-colors">
          ← Back to dashboard
        </Link>
      </div>
    </div>
  );
}

export default function TransferPage() {
  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      <nav className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between">
        <Link href="/" className="font-semibold text-lg text-blue-600 tracking-tight">SecurePay</Link>
        <Link href="/dashboard" className="text-sm text-slate-600 hover:text-blue-600 transition-colors">Dashboard</Link>
      </nav>
      <div className="flex-1 flex items-center justify-center px-4 py-8">
        <Suspense fallback={<div className="text-slate-400 text-sm">Loading…</div>}>
          <TransferForm />
        </Suspense>
      </div>
    </div>
  );
}
