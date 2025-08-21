"use client"
import React from "react"
import { getUserInfo } from "../../services/auth"
import { useState, useEffect } from "react"
import { inventoryApi } from "../../services/inventoryApi"
import ItemsList from "./ItemsList"
import StockReport from "./StockReport"
import DashboardStats from "./DashboardStats"
import AddEditItemModal from "./AddEditItemModal"
import StockMovementModal from "./StockMovementModal"
import "./InventoryDashboard.css"
import { logout } from "../../services/auth"


const handleLogout = async () => {
    try {
      await logout();
      window.location.href = '/login';
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

const InventoryDashboard = () => {
  const [activeTab, setActiveTab] = useState("dashboard")
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAddModal, setShowAddModal] = useState(false)
  const [showStockModal, setShowStockModal] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)
  const [stockMovementType, setStockMovementType] = useState("inward")
  const [stats, setStats] = useState({
    totalItems: 0,
    lowStockItems: 0,
    totalValue: 0,
    outOfStockItems: 0,
  })
  const [user, setUser] = React.useState(null);
  
  React.useEffect(() => {
    fetchUserInfo();
  }, []);
  const fetchUserInfo = async () => {
      try {
        const userData = await getUserInfo();
        setUser(userData);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    };
  useEffect(() => {
    fetchItems()
  },[])

  const fetchItems = async () => {
    try {
      setLoading(true)
      const response = await inventoryApi.getAllItems()
      const itemsData = response.data
      setItems(itemsData)
      console.log("Fetched items:", itemsData)
      calculateStats(itemsData)
    } catch (error) {
      console.error("Failed to fetch items:", error)
    } finally {
      setLoading(false)
    }
  }

  const calculateStats = (itemsData) => {
    const totalItems = itemsData.length
    const lowStockItems = itemsData.filter((item) => item.quantity > 0 && item.quantity < 10).length
    const outOfStockItems = itemsData.filter((item) => item.quantity === 0).length
    const totalValue = itemsData.reduce((sum, item) => sum + item.price * item.quantity, 0)

    setStats({
      totalItems,
      lowStockItems,
      outOfStockItems,
      totalValue,
    })
  }

  const handleStockMovement = (item, type) => {
    setSelectedItem(item)
    setStockMovementType(type)
    setShowStockModal(true)
  }

  const handleStockMovementComplete = () => {
    setShowStockModal(false)
    setSelectedItem(null)
    fetchItems()
  }

  const handleItemSaved = () => {
    setShowAddModal(false)
    fetchItems()
  }

  const renderContent = () => {
    switch (activeTab) {
      case "dashboard":
        return <DashboardStats stats={stats} onAddItem={() => setShowAddModal(true)} user={user} />
      case "items":
        return (
          <ItemsList
            items={items}
            loading={loading}
            onStockMovement={handleStockMovement}
            onRefresh={fetchItems}
            onAddItem={() => setShowAddModal(true)}
            user={user}
          />
        )
      case "reports":
        return <StockReport />
      default:
        return <DashboardStats stats={stats} onAddItem={() => setShowAddModal(true)} />
    }
  }

  return (
    <div className="inventory-dashboard">
      
      <div className="dashboard-container" >
        {/* <Sidebar /> */}
        <main className="main-content">
          <div className="content-header">
            <div className="tab-navigation">
              <button
                className={`tab-btn ${activeTab === "dashboard" ? "active" : ""}`}
                onClick={() => setActiveTab("dashboard")}
              >
                Dashboard
              </button>
              <button
                className={`tab-btn ${activeTab === "items" ? "active" : ""}`}
                onClick={() => setActiveTab("items")}
              >
                Items
              </button>
              <button
                className={`tab-btn ${activeTab === "reports" ? "active" : ""}`}
                onClick={() => setActiveTab("reports")}
              >
                Reports
              </button>
              <button className={`logout-button`}
                      onClick={handleLogout}>
                Logout
              </button>
            </div>
          </div>

          <div className="content-body">{renderContent()}</div>
        </main>
      </div>

      {/* Modals */}
      <AddEditItemModal isOpen={showAddModal} onClose={() => setShowAddModal(false)} onSuccess={handleItemSaved} />

      <StockMovementModal
        isOpen={showStockModal}
        onClose={() => setShowStockModal(false)}
        item={selectedItem}
        type={stockMovementType}
        onSuccess={handleStockMovementComplete}
      />
    </div>
  )
}

export default InventoryDashboard
