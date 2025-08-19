import api from './api';
import config from './config';

export const handleOAuthCallback = async (fullUrl) => {
  try {
    const response = await fetch(`${config.API_BASE_URL}/login/oauth2/code/google${window.location.search}`, {
      method: 'GET',
      credentials: 'include', // Important for cookies
      headers: {
        'Accept': 'application/json'
      }
    });

    const data = await response.json();

    if (data.access_token) {
      localStorage.setItem('token', data.access_token);
      const userInfo = await getUserInfo();
      console.log('User info:', userInfo);
      return { success: true, user: userInfo };
    }
    return { success: false };
  } catch (error) {
    console.error('OAuth callback failed:', error);
    throw error;
  }
};


export const login = async (username, password) => {
  try {
    const response = await api.post('/login', `username=${username}&password=${password}`, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    if (response.data && response.data.access_token) {
      localStorage.setItem('token', response.data.access_token);
      return { success: true };
    }
    return { success: false };
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};

export const logout = () => {
  localStorage.removeItem('token');
  return api.get('/logout');
};

export const googleLogin = () => {
  console.log('Redirecting to Google login...');
  window.location.href = '/oauth2/authorization/google';
};

export const getUserInfo = async () => {
  try {
    const response = await api.get('/items/info');
    return response.data;
  } catch (error) {
    console.error('Failed to get user info:', error);
    throw error;
  }
};

export const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};