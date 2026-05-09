/**
 * API Service Layer — centralizes all backend calls.
 * FLAW #23 FIX: Replaces hardcoded data with real API calls.
 */

const API_BASE = import.meta.env.VITE_API_URL || '';

if (import.meta.env.DEV) {
  console.log(`🚀 NSIP API Base: ${API_BASE || 'Relative (Vite Proxy)'}`);
}

async function request(path: string, options?: RequestInit) {
  const token = localStorage.getItem('nsip_token');
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'Network error' }));
    throw new Error(error.error || `HTTP ${response.status}`);
  }

  return response.json();
}

export const api = {
  // Auth
  login: (email: string, password: string) =>
    request('/api/v1/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),

  register: (data: { nationalId: string; fullName: string; email: string; password: string; role: string }) =>
    request('/api/v1/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  // Advisor Sessions
  getAdvisors: () => request('/api/v1/learning/advisors'),
  bookSession: (data: any) => request('/api/v1/learning/sessions/book', { method: 'POST', body: JSON.stringify(data) }),
  getCustomerSessions: (userId: string) => request(`/api/v1/learning/sessions/customer/${userId}`),
  cancelSession: (sessionId: string, reason: string) => 
    request(`/api/v1/learning/sessions/${sessionId}/customer-cancel`, { method: 'PATCH', body: JSON.stringify({ reason }) }),
  rescheduleSession: (sessionId: string, newTime: string) => 
    request(`/api/v1/learning/sessions/${sessionId}/reschedule`, { method: 'PATCH', body: JSON.stringify({ newTime }) }),
  submitReview: (sessionId: string, data: any) => 
    request(`/api/v1/learning/sessions/${sessionId}/review`, { method: 'POST', body: JSON.stringify(data) }),

  // Claims
  submitClaim: (userId: string, claimType: string, amount: string, description: string) =>
    request('/api/v1/claims', { method: 'POST', body: JSON.stringify({ userId, claimType, amount, description }) }),

  getUserClaims: (userId: string) => request(`/api/v1/claims/user/${userId}`),
  getPendingClaims: () => request('/api/v1/claims/pending'),
  approveClaim: (claimId: string) => request(`/api/v1/claims/${claimId}/approve`, { method: 'PATCH' }),
  rejectClaim: (claimId: string) => request(`/api/v1/claims/${claimId}/reject`, { method: 'PATCH' }),

  // Rewards
  getPointsBalance: (userId: string) => request(`/api/v1/rewards/balance/${userId}`),

  // Reviews
  submitFeatureReview: (userId: string, featureName: string, rating: number, comment: string) =>
    request('/api/v1/reviews', { method: 'POST', body: JSON.stringify({ userId, featureName, rating: String(rating), comment }) }),

  // Payments
  getUserRepayments: (userId: string) => request(`/api/v1/payments/repayments/${userId}`),

  // LiveKit Assistant
  getLiveKitToken: (roomName?: string) => 
    request('/api/v1/auth/livekit/token', { method: 'POST', body: JSON.stringify({ roomName }) }),
};
