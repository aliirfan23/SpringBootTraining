// src/components/auth/OAuthCallback.js
import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const OAuthCallback = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { refreshAuth } = useAuth();

  useEffect(() => {
    const token = params.get('token');
    if (token) {
      localStorage.setItem('token', token);
      refreshAuth().then(() => {
        navigate('/dashboard');
      });
    } else {
      navigate('/login');
    }
  }, [params, refreshAuth, navigate]);

  return <div>Processing login...</div>;
};

export default OAuthCallback;
