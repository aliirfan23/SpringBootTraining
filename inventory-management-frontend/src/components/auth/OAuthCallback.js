// src/components/auth/OAuthCallback.js
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getUserInfo } from '../../services/auth'; // Adjust the import path as necessary

// Make sure to import getUserInfo if it exists, or define it here
// import { getUserInfo } from 'path-to-getUserInfo';


const OAuthCallback = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { refreshAuth } = useAuth();

  useEffect(() => {
    const processAuth = async () => {
      const token = params.get('token');
      if (token) {
        localStorage.setItem('token', token);
        const userInfo = await getUserInfo();
        console.log('User info:', userInfo);
        await refreshAuth();
        navigate('/inventory');
      } else {
        navigate('/login');
      }
    };
    processAuth();
  }, [params, refreshAuth, navigate]);

  return <div>Processing login...</div>;
};

export default OAuthCallback;
