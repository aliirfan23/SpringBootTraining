"use client"
import Button from "../ui/Button"
import "./DashboardStats.css"

const DashboardStats = ({ stats, onAddItem, user}) => {
  return (
    <div className="dashboard-stats">
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">📦</div>
          <div className="stat-content">
            <h3>Total Items</h3>
            <p className="stat-number">{stats.totalItems}</p>
            <span className="stat-label">Active inventory items</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">⚠️</div>
          <div className="stat-content">
            <h3>Low Stock Alert</h3>
            <p className="stat-number text-warning">{stats.lowStockItems}</p>
            <span className="stat-label">Items below 10 units</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">❌</div>
          <div className="stat-content">
            <h3>Out of Stock</h3>
            <p className="stat-number text-danger">{stats.outOfStockItems}</p>
            <span className="stat-label">Items with 0 quantity</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-content">
            <h3>Total Value</h3>
            <p className="stat-number text-success">${stats.totalValue.toFixed(2)}</p>
            <span className="stat-label">Current inventory value</span>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <h3>Quick Actions</h3>
        <div className="actions-grid">
          <Button onClick={onAddItem} disabled={!user?.roles?.includes("ADMIN")} className="btn-primary">
            ➕ Add New Item
          </Button>
          <Button disabled={!user?.roles?.includes("ADMIN")} className="btn-secondary">📊 View Reports</Button>
          <Button className="btn-secondary">📥 Import Items</Button>
          <Button className="btn-secondary">📤 Export Data</Button>
        </div>
      </div>
    </div>
  )
}

export default DashboardStats
