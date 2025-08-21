// import React from 'react';
// import { useNavigate } from 'react-router-dom';
// import { useAuth } from '../context/AuthContext';
import Login from '../components/auth/Login';

const LoginPage = () => {
  // const { isAuthenticated } = useAuth();
  // const navigate = useNavigate();

  // React.useEffect(() => {
  //   if (isAuthenticated) {
  //     navigate('/inventory', { replace: true });
  //   }
  // }, [isAuthenticated, navigate]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Login />
      </div>
    </div>
  );
};

export default LoginPage;