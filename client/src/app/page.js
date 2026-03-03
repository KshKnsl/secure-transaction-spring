import Link from 'next/link';

export default function Home() {
  return (
    <main className="min-h-screen flex flex-col">
      <nav className="border-b border-slate-200 bg-white px-6 py-4 flex items-center justify-between">
        <span className="font-semibold text-lg text-blue-600 tracking-tight">SecurePay</span>
        <div className="flex gap-3">
          <Link href="/login" className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-blue-600 transition-colors">Log in</Link>
          <Link href="/register" className="px-4 py-2 text-sm font-medium bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">Get started</Link>
        </div>
      </nav>
      <section className="flex-1 flex flex-col items-center justify-center text-center px-6 py-24">
        <span className="inline-block mb-4 px-3 py-1 text-xs font-semibold bg-blue-50 text-blue-700 rounded-full uppercase tracking-wide">Ledger Service</span>
        <h1 className="text-5xl font-bold text-slate-900 mb-6 leading-tight max-w-2xl">Secure, reliable<br />money transfers</h1>
        <p className="text-slate-500 text-lg mb-10 max-w-md">Create accounts, transfer funds, and track every transaction with double-entry ledger precision.</p>
        <div className="flex gap-4">
          <Link href="/register" className="px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm">Create account</Link>
          <Link href="/login" className="px-6 py-3 border border-slate-300 text-slate-700 font-medium rounded-lg hover:border-blue-400 hover:text-blue-600 transition-colors">Sign in</Link>
        </div>
      </section>
      <section className="bg-white border-t border-slate-100 py-16 px-6">
        <div className="max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="p-6 rounded-xl border border-slate-200 hover:border-blue-200 transition-colors">
            <h3 className="font-semibold text-slate-800 mb-2">JWT Auth</h3>
            <p className="text-sm text-slate-500">Secure cookie-based authentication with automatic expiry.</p>
          </div>
          <div className="p-6 rounded-xl border border-slate-200 hover:border-blue-200 transition-colors">
            <h3 className="font-semibold text-slate-800 mb-2">Multi-account</h3>
            <p className="text-sm text-slate-500">Create multiple INR accounts and manage balances independently.</p>
          </div>
          <div className="p-6 rounded-xl border border-slate-200 hover:border-blue-200 transition-colors">
            <h3 className="font-semibold text-slate-800 mb-2">Idempotent transfers</h3>
            <p className="text-sm text-slate-500">Duplicate-safe transactions with idempotency key protection.</p>
          </div>
        </div>
      </section>
      <footer className="text-center py-6 text-sm text-slate-400 border-t border-slate-100">SecurePay – backed by Spring Boot &amp; MongoDB</footer>
    </main>
  );
}
