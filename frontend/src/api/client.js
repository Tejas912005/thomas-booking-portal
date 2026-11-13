import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Standardized backend error payload fallback
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data);
    }
    return Promise.reject({
      status: error.response ? error.response.status : 500,
      error: 'Network Error',
      message: error.message || 'Unable to connect to the reservation service. Please verify your connection.',
    });
  }
);

export default api;

