import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Sidebar from '../components/layout/Sidebar';
import Header from '../components/layout/Header';
// import InventoryManager from './inventory/InventoryManager';
// import StockInward from './inventory/StockInward';
// import StockOutward from './inventory/StockOutward';
// import StockReports from './reports/StockReports';
// import UserManagement from './admin/UserManagement';

const Dashboard = () => {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar user={user} />
      <Header user={user} />
      
      <main className="ml-64 pt-16 min-h-screen">
        <div className="p-6">
          {/* <Routes>
            <Route path="/inventory" element={<InventoryManager />} />
            <Route path="/stock-inward" element={<StockInward />} />
            <Route path="/stock-outward" element={<StockOutward />} />
            <Route path="/reports" element={<StockReports />} />
            {user?.roles?.includes('ADMIN') && (
              <Route path="/users" element={<UserManagement />} />
            )}
            <Route path="/" element={<Navigate to="/inventory" replace />} />
          </Routes> */}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;