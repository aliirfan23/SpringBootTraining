import React from 'react';
import { getUserInfo, logout } from '../../services/auth';
import './Header.css'; 
import logo from '../ui/logo.png'; 

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
      await logout();
      window.location.href = '/login';
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <>
      <div className="header-accent-bar">
        <div className="header-logo">
          <img src={logo} alt="Logo" className="logo-image" />
        </div>
        <span className="logo-text">Inventory Management</span>
      
        {/* {user && (
          <button className="logout-btn"
                      onClick={handleLogout}>
                Logout
          </button>
        )} */}
      </div>

      {/* <header className="header-main">
        <div className="header-content">
          <h1 className="header-title">Dashboard</h1>

          <div className="header-user-info">
            {user ? (
              <>
                <div className="user-text">
                  <div className="username">{user.username}</div>
                  <div className="user-roles">
                    {user.roles?.length ? user.roles.join(', ') : 'User'}
                  </div>
                </div>
                <div className="user-avatar">
                  {user.username.charAt(0).toUpperCase()}
                </div>
              </>
            ) : (
              <Link to="/login" className="login-link">
                Login
              </Link>
            )}
          </div>
        </div>
      </header> */}
    </>
  );
};

export default Header;
