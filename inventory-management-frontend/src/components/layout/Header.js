import React from 'react';
import { Link } from 'react-router-dom';
import { getUserInfo, logout } from '../../services/auth';

const Header = () => {
  const [user, setUser] = React.useState(null);
  
  React.useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const userData = await getUserInfo();
        setUser(userData);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    };
    
    fetchUserInfo();
  }, []);
  
  const handleLogout = async () => {
    try {
        console.log('Logging out...');
      await logout();
      window.location.href = '/login';
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };
  
  return (
    <header className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <div className="bg-gray-200 border-2 border-dashed rounded-xl w-8 h-8" />
              <span className="ml-2 text-xl font-bold text-gray-800">Inventory Manager</span>
            </div>
          </div>
          
          <div className="flex items-center">
            {user ? (
              <div className="flex items-center">
                <div className="mr-3 text-sm text-gray-700">
                  <div className="font-medium">{user.username}</div>
                  <div className="text-gray-500">
                    {user.roles && user.roles.length > 0 ? user.roles.join(', ') : 'User'}
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="inline-flex items-center px-3 py-1.5 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex space-x-2">
                <Link
                  to="/login"
                  className="inline-flex items-center px-3 py-1.5 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
                >
                  Login
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;