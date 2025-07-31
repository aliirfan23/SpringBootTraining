"use client"

import { useState, useEffect } from "react"
import { inventoryApi } from "../../services/inventoryApi"
import Button from "../ui/Button"
import Input from "../ui/Input"
import Loader from "../ui/Loader"
import AddEditItemModal from "./AddEditItemModal"
import "./ItemsList.css"

const ItemsList = ({ items, loading, onStockMovement, onRefresh, onAddItem, user }) => {
  const [filteredItems, setFilteredItems] = useState([])
  const [searchTerm, setSearchTerm] = useState("")
  const [showEditModal, setShowEditModal] = useState(false)
  const [selectedItem, setSelectedItem] = useState(null)

  useEffect(() => {
    const filtered = items.filter(
      (item) =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.supplier.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.status.toLowerCase().includes(searchTerm.toLowerCase()),
    )
    setFilteredItems(filtered)
  }, [items, searchTerm])

  const handleDelete = async (itemId) => {
    if (window.confirm("Are you sure you want to delete this item?")) {
      try {
        await inventoryApi.deleteItem(itemId)
        onRefresh()
      } catch (error) {
        console.error("Failed to delete item:", error)
        alert("Failed to delete item")
      }
    }
  }

  const handleEdit = (item) => {
    setSelectedItem(item)
    setShowEditModal(true)
  }

  const getStatusBadge = (status) => {
    const statusClasses = {
      ACTIVE: "status-active",
      INACTIVE: "status-inactive",
      OUT_OF_STOCK: "status-warning",
      DISCONTINUED: "status-secondary",
    }
    return statusClasses[status] || "status-secondary"
  }

  const getQuantityBadge = (quantity) => {
    if (quantity === 0) return "quantity-danger"
    if (quantity < 10) return "quantity-warning"
    return "quantity-success"
  }

  if (loading) {
    return (
      <div className="items-list-container">
        <div className="loading-container">
          <Loader />
          <p>Loading items...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="items-list-container">
      <div className="items-header">
        <div className="header-left">
          <h2>Inventory Items</h2>
          <p>Manage your inventory items and stock levels</p>
        </div>
        <div className="header-right" >
            <input
              type="text"
              placeholder="Search items..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            
            />
          <button onClick={onAddItem} disabled={!user?.roles?.includes("ADMIN")} className="add-item-button">
            ➕ Add Item
          </button>
        </div>
      </div>

      {filteredItems.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No items found</h3>
          <p>{searchTerm ? "Try adjusting your search terms" : "Start by adding your first inventory item"}</p>
        </div>
      ) : (
        <div className="items-table-container">
          <table className="items-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Supplier</th>
                <th>Status</th>
                <th>Last Updated</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredItems.map((item) => (
                <tr key={item.itemId}>
                  <td className="item-name">{item.name}</td>
                  <td className="item-price">${item.price.toFixed(2)}</td>
                  <td>
                    <span className={`quantity-badge ${getQuantityBadge(item.quantity)}`}>{item.quantity} units</span>
                  </td>
                  <td>{item.supplier}</td>
                  <td>
                    <span className={`status-badge ${getStatusBadge(item.status)}`}>
                      {item.status.replace("_", " ")}
                    </span>
                  </td>
                  <td>{new Date(item.updatedAt).toLocaleDateString()}</td>
                  <td className="actions-cell">
                    <div className="action-buttons">
                      <button
                        className="action-btn btn-success"
                        onClick={() => onStockMovement(item, "inward")}
                        title="Stock Inward"
                      >
                        ➕
                      </button>
                      <button
                        className="action-btn btn-warning"
                        onClick={() => onStockMovement(item, "outward")}
                        disabled={item.quantity === 0}
                        title="Stock Outward"
                      >
                        ➖
                      </button>
                      <button disabled={!user?.roles?.includes("ADMIN")}
                        className="action-btn btn-primary" 
                        onClick={() => handleEdit(item)} 
                        title="Edit Item">
                        ✏️
                      </button>
                      <button
                        className="action-btn btn-danger"
                        onClick={() => handleDelete(item.itemId)}
                        title="Delete Item"
                        disabled={!user?.roles?.includes("ADMIN")}
                      >
                        🗑️
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <AddEditItemModal
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false)
          setSelectedItem(null)
        }}
        item={selectedItem}
        onSuccess={() => {
          setShowEditModal(false)
          setSelectedItem(null)
          onRefresh()
        }}
      />
    </div>
  )
}

export default ItemsList
