"use client"
import { useAuth } from "../context/AuthContext"
import InventoryDashboard from "../components/inventory/InventoryDashboard"
import { Navigate } from "react-router-dom"

const InventoryPage = () => {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loader"></div>
        <p>Loading...</p>
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <InventoryDashboard />
}

export default InventoryPage
