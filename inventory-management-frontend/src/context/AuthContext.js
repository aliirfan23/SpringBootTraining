import React, { createContext, useState, useEffect, useContext } from 'react';
import { isAuthenticated, getUserInfo } from '../services/auth';
import { initCsrf } from '../services/auth';
const AuthContext = createContext();



export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const checkAuth = async () => {
    setLoading(true);
    if (isAuthenticated()) {
      try {
        const userData = await getUserInfo();
        setUser(userData);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    try {
      // Fetch CSRF token cookie from backend
      initCsrf();
      console.log("CSRF token initialized");
    } catch (err) {
      console.error("Failed to initialize CSRF token:", err);
    }
    checkAuth();
  }, []);

  const value = {
    user,
    loading,
    isAuthenticated: !!user,
    setUser,
    refreshAuth: checkAuth // Add this function to context
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};