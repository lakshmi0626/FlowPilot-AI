import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('flowpilot_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Don't auto-logout if we're attempting login
      if (!window.location.pathname.includes('/login') && !window.location.pathname.includes('/register')) {
        console.warn('Unauthorized access, session expired.');
      }
    }
    return Promise.reject(error);
  }
);

export default api;
