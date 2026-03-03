'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { toast } from 'sonner';
import { auth, accounts } from '@/lib/api';

function AccountCard({ account, onRefresh }) {
  const [balance, setBalance] = useState(null);
  const [loadingBal, setLoadingBal] = useState(false);

  const fetchBalance = useCallback(async () => {
    setLoadingBal(true);
    try {
      const data = await accounts.balance(account.id);
      setBalance(data.balance);
    } catch (err) {
      setBalance('—');
      toast.error(`Balance fetch failed: ${err.message}`);
    } finally {
      setLoadingBal(false);
    }
  }, [account.id]);

  useEffect(() => {
    fetchBalance();
  }, [fetchBalance]);

  const statusColor = {
    ACTIVE: 'bg-green-100 text-green-700',
    FROZEN: 'bg-yellow-100 text-yellow-700',
    CLOSED: 'bg-slate-100 text-slate-500',
  }[account.status] || 'bg-slate-100 text-slate-500';

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-5 flex flex-col gap-3 hover:border-blue-200 transition-colors">
      <div className="flex items-center justify-between">
        <span className="text-xs font-mono text-slate-400 truncate max-w-[180px]">{account.id}</span>
        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${statusColor}`}>
          {account.status}
        </span>
      </div>
      <div className="flex items-end justify-between">
        <div>
          <p className="text-xs text-slate-400 mb-0.5">Balance</p>
          {loadingBal ? (
            <p className="text-xl font-semibold text-slate-400 animate-pulse">Loading…</p>
          ) : (
            <p className="text-2xl font-bold text-slate-900">
              ₹{typeof balance === 'number' ? balance.toLocaleString('en-IN', { minimumFractionDigits: 2 }) : balance}
            </p>
          )}
        </div>
        <div className="flex gap-2">
          <button
            onClick={fetchBalance}
            className="text-xs px-3 py-1.5 border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50 transition-colors"
          >
            Refresh
          </button>
          <Link
            href={`/dashboard/transfer?from=${account.id}`}
            className="text-xs px-3 py-1.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Transfer
          </Link>
        </div>
      </div>
      <p className="text-xs text-slate-400">{account.currency} · Created {new Date(account.createdAt).toLocaleDateString()}</p>
    </div>
  );
}

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [accountList, setAccountList] = useState([]);
  const [loadingAccounts, setLoadingAccounts] = useState(true);
  const [creatingAccount, setCreatingAccount] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (!stored) {
      router.push('/login');
      return;
    }
    setUser(JSON.parse(stored));
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    setLoadingAccounts(true);
    try {
      const data = await accounts.list();
      setAccountList(data);
    } catch (err) {
      if (err.message.includes('401') || err.message.toLowerCase().includes('unauthorized')) {
        router.push('/login');
      } else {
        toast.error(err.message);
      }
    } finally {
      setLoadingAccounts(false);
    }
  };

  const handleCreateAccount = async () => {
    setCreatingAccount(true);
    const toastId = toast.loading('Creating account…');
    try {
      const newAccount = await accounts.create();
      setAccountList((prev) => [...prev, newAccount]);
      toast.success('New account created!', { id: toastId });
    } catch (err) {
      toast.error(err.message, { id: toastId });
    } finally {
      setCreatingAccount(false);
    }
  };

  const handleLogout = async () => {
    auth.logout().catch(() => {});
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/');
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      <nav className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between">
        <Link href="/" className="font-semibold text-lg text-blue-600 tracking-tight">SecurePay</Link>
        <div className="flex items-center gap-4">
          {user && (
            <span className="text-sm text-slate-600 hidden sm:block">
              {user.name}
            </span>
          )}
          <button
            onClick={handleLogout}
            className="text-sm px-4 py-2 border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50 hover:text-red-600 transition-colors"
          >
            Log out
          </button>
        </div>
      </nav>

      <div className="max-w-3xl mx-auto w-full px-4 py-8 flex flex-col gap-8">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">
            {user ? `Hello, ${user.name.split(' ')[0]}` : 'Dashboard'}
          </h1>
          <p className="text-slate-500 text-sm mt-1">Manage your accounts and send money</p>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          <button
            onClick={handleCreateAccount}
            disabled={creatingAccount}
            className="flex flex-col items-center gap-2 p-4 bg-white border border-slate-200 rounded-xl hover:border-blue-300 hover:bg-blue-50 transition-colors disabled:opacity-50"
          >
            <span className="text-2xl">＋</span>
            <span className="text-sm font-medium text-slate-700">{creatingAccount ? 'Creating…' : 'New Account'}</span>
          </button>
          <Link
            href="/dashboard/transfer"
            className="flex flex-col items-center gap-2 p-4 bg-white border border-slate-200 rounded-xl hover:border-blue-300 hover:bg-blue-50 transition-colors"
          >
            <span className="text-2xl">↗</span>
            <span className="text-sm font-medium text-slate-700">Send Money</span>
          </Link>
          <button
            onClick={fetchAccounts}
            className="flex flex-col items-center gap-2 p-4 bg-white border border-slate-200 rounded-xl hover:border-blue-300 hover:bg-blue-50 transition-colors"
          >
            <span className="text-2xl">↺</span>
            <span className="text-sm font-medium text-slate-700">Refresh</span>
          </button>
        </div>

        <div>
          <h2 className="text-base font-semibold text-slate-700 mb-4">
            Your Accounts ({accountList.length})
          </h2>
          {loadingAccounts ? (
            <div className="grid gap-4">
              {[1, 2].map((i) => (
                <div key={i} className="bg-white border border-slate-200 rounded-xl p-5 animate-pulse h-28" />
              ))}
            </div>
          ) : accountList.length === 0 ? (
            <div className="bg-white border border-dashed border-slate-300 rounded-xl p-10 text-center">
              <p className="text-slate-500 text-sm mb-3">No accounts yet</p>
              <button
                onClick={handleCreateAccount}
                disabled={creatingAccount}
                className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
              >
                {creatingAccount ? 'Creating…' : 'Create your first account'}
              </button>
            </div>
          ) : (
            <div className="grid gap-4">
              {accountList.map((account) => (
                <AccountCard key={account.id} account={account} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
