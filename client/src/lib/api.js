function getAuthHeader() {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request(path, options = {}) {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...getAuthHeader(), ...options.headers },
    ...options,
  });

  let data;
  try {
    data = await res.json();
  } catch {
    data = {};
  }

  if (!res.ok) {
    throw new Error(data.message || `Request failed with status ${res.status}`);
  }

  return data;
}

export const auth = {
  register: (body) =>
    request('/api/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) =>
    request('/api/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  logout: () =>
    request('/api/auth/logout', { method: 'POST' }),
};

export const accounts = {
  create: () =>
    request('/api/accounts', { method: 'POST' }),
  list: () =>
    request('/api/accounts'),
  balance: (accountId) =>
    request(`/api/accounts/balance/${accountId}`),
};

export const transactions = {
  create: (body) =>
    request('/api/transactions', { method: 'POST', body: JSON.stringify(body) }),
};
