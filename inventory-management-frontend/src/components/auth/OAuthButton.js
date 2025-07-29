import React from 'react';
import './OAuthButton.css'; // Import the CSS
import { googleLogin } from '../../services/auth';

const OAuthButton = ({ provider = 'google', text = 'Continue with Google' }) => {
  const handleLogin = () => {
    if (provider === 'google') {
      googleLogin();
    }
  };

  return (
    <div className="oauth-container">
      <div className="oauth-divider">
        <hr className="line" />
        <span className="divider-text">Or</span>
        <hr className="line" />
      </div>

      <div className="oauth-button-container">
        <button className="google-login-button" onClick={handleLogin}>
          <img
            src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
            alt="Google Logo"
            className="google-icon"
          />
          <span className="google-text">{text}</span>
        </button>
      </div>
    </div>
  );
};

export default OAuthButton;
