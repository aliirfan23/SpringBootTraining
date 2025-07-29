import React from 'react';
import { useAuth } from '../context/AuthContext';
import Header from '../components/layout/Header';

const Dashboard = () => {
  const { user } = useAuth();

  return (
    <div className="dashboard-container">
      <Header />
      
      <main className="dashboard-main">
        <div className="container mx-auto px-4 py-6">
          <div className="border-2 border-dashed border-gray-300 rounded-lg p-6">
            <h1 className="text-2xl font-bold text-gray-800 mb-4">Welcome to Inventory Manager</h1>
            
            {user && (
              <div className="bg-white p-4 rounded-lg shadow mb-6">
                <p className="text-gray-600 mb-2">
                  <span className="font-medium">Username:</span> {user.username}
                </p>
                <p className="text-gray-600">
                  <span className="font-medium">Roles:</span> {user.roles && user.roles.length > 0 ? user.roles.join(', ') : 'No roles assigned'}
                </p>
              </div>
            )}
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-white p-6 rounded-lg shadow">
                <div className="w-16 h-16 bg-gray-200 border-2 border-dashed rounded-xl mx-auto"></div>
                <h3 className="mt-4 text-lg font-medium text-gray-800 text-center">Manage Items</h3>
                <p className="mt-2 text-gray-600 text-center">View and manage your inventory items</p>
              </div>
              
              <div className="bg-white p-6 rounded-lg shadow">
                <div className="w-16 h-16 bg-gray-200 border-2 border-dashed rounded-xl mx-auto"></div>
                <h3 className="mt-4 text-lg font-medium text-gray-800 text-center">Stock Reports</h3>
                <p className="mt-2 text-gray-600 text-center">Generate detailed stock reports</p>
              </div>
              
              <div className="bg-white p-6 rounded-lg shadow">
                <div className="w-16 h-16 bg-gray-200 border-2 border-dashed rounded-xl mx-auto"></div>
                <h3 className="mt-4 text-lg font-medium text-gray-800 text-center">User Management</h3>
                <p className="mt-2 text-gray-600 text-center">Manage users and permissions</p>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <footer className="dashboard-footer">
        <div className="container mx-auto px-4">
          <div className="text-center text-gray-500 text-sm">
            &copy; 2023 Inventory Manager. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Dashboard;