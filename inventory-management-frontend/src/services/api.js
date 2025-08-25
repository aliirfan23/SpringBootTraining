import axios from 'axios';
// import config from './config';

// Helper: read CSRF token from cookies
// function getCookie(name) {
//   const value = `; ${document.cookie}`;
//   const parts = value.split(`; ${name}=`);
//   if (parts.length === 2) return parts.pop().split(';').shift();
// }

const api = axios.create({
  baseURL: "",
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

// Request interceptor
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // Attach CSRF token if exists
  // const csrfToken = getCookie('XSRF-TOKEN');
  // if (csrfToken) {
  //   config.headers['X-XSRF-TOKEN'] = csrfToken;
  // }

  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response interceptor
// api.interceptors.response.use((response) => {
//   return response;
// }, async (error) => {
//   const originalRequest = error.config;

//   if (error.response.status === 401 && !originalRequest._retry) {
//     originalRequest._retry = true;

//     try {
//       const response = await api.post('/token/refresh');
//       const { token } = response.data;
//       localStorage.setItem('token', token);

//       originalRequest.headers.Authorization = `Bearer ${token}`;
//       return api(originalRequest);
//     } catch (refreshError) {
//       localStorage.removeItem('token');
//       window.location.href = '/login';
//       return Promise.reject(refreshError);
//     }
//   }

//   return Promise.reject(error);
// });
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear auth data
      localStorage.removeItem('token');
      alert("Session expired. Redirecting to login...");
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
