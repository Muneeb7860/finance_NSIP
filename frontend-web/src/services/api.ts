import axios from 'axios';

const api = {
  login: (email: string, password: string) => axios.post('/api/v1/auth/login', { email, password }),
  getContributions: () => axios.get('/api/v1/contributions/summary'),
  getClaims: () => axios.get('/api/v1/claims/list'),
};

export default api;
