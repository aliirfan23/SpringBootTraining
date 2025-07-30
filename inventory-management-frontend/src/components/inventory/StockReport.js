"use client"

import { useState, useEffect } from "react"
import { inventoryApi } from "../../services/inventoryApi"
import Button from "../ui/Button"
import Loader from "../ui/Loader"
import "./StockReport.css"

const StockReport = () => {
  const [reportData, setReportData] = useState([])
  const [loading, setLoading] = useState(true)
  const [summary, setSummary] = useState({
    totalItems: 0,
    totalValue: 0,
    lowStockItems: 0,
    outOfStockItems: 0,
  })

  useEffect(() => {
    fetchStockReport()
  }, [])

  const fetchStockReport = async () => {
    try {
      setLoading(true)
      const response = await inventoryApi.getStockReport()
      const data = response.data

      // Calculate summary statistics
      const totalItems = data.length
      const totalValue = data.reduce((sum, item) => sum + item.price * item.quantity, 0)
      const lowStockItems = data.filter((item) => item.quantity > 0 && item.quantity < 10).length
      const outOfStockItems = data.filter((item) => item.quantity === 0).length

      setReportData(data)
      setSummary({
        totalItems,
        totalValue,
        lowStockItems,
        outOfStockItems,
      })
    } catch (error) {
      console.error("Failed to fetch stock report:", error)
    } finally {
      setLoading(false)
    }
  }

  const exportToCSV = () => {
    const headers = [
      "Item ID",
      "Name",
      "Price",
      "Quantity",
      "Supplier",
      "Status",
      "Total Value",
      "Created At",
      "Updated At",
    ]

    const csvContent = [
      headers.join(","),
      ...reportData.map((item) =>
        [
          item.itemId,
          `"${item.name}"`,
          item.price,
          item.quantity,
          `"${item.supplier}"`,
          item.status,
          (item.price * item.quantity).toFixed(2),
          item.createdAt,
          item.updatedAt,
        ].join(","),
      ),
    ].join("\n")

    const blob = new Blob([csvContent], { type: "text/csv" })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `stock-report-${new Date().toISOString().split("T")[0]}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
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
      <div className="stock-report-container">
        <div className="loading-container">
          <Loader />
          <p>Generating stock report...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="stock-report-container">
      {/* Summary Cards */}
      <div className="report-summary">
        <div className="summary-card">
          <div className="summary-icon">📦</div>
          <div className="summary-content">
            <h3>Total Items</h3>
            <p className="summary-number">{summary.totalItems}</p>
            <span>Items in inventory</span>
          </div>
        </div>

        <div className="summary-card">
          <div className="summary-icon">💰</div>
          <div className="summary-content">
            <h3>Total Value</h3>
            <p className="summary-number">${summary.totalValue.toFixed(2)}</p>
            <span>Current inventory value</span>
          </div>
        </div>

        <div className="summary-card">
          <div className="summary-icon">⚠️</div>
          <div className="summary-content">
            <h3>Low Stock</h3>
            <p className="summary-number text-warning">{summary.lowStockItems}</p>
            <span>Items below 10 units</span>
          </div>
        </div>

        <div className="summary-card">
          <div className="summary-icon">❌</div>
          <div className="summary-content">
            <h3>Out of Stock</h3>
            <p className="summary-number text-danger">{summary.outOfStockItems}</p>
            <span>Items with 0 quantity</span>
          </div>
        </div>
      </div>

      {/* Report Table */}
      <div className="report-table-container">
        <div className="report-header">
          <div>
            <h2>Stock Report</h2>
            <p>Complete inventory report with current stock levels and values</p>
          </div>
          <Button onClick={exportToCSV} className="btn-primary">
            📥 Export CSV
          </Button>
        </div>

        <div className="table-wrapper">
          <table className="report-table">
            <thead>
              <tr>
                <th>Item ID</th>
                <th>Name</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Total Value</th>
                <th>Supplier</th>
                <th>Status</th>
                <th>Last Updated</th>
              </tr>
            </thead>
            <tbody>
              {reportData.map((item) => (
                <tr key={item.itemId}>
                  <td className="item-id">{item.itemId}</td>
                  <td className="item-name">{item.name}</td>
                  <td className="item-price">${item.price.toFixed(2)}</td>
                  <td>
                    <span className={`quantity-badge ${getQuantityBadge(item.quantity)}`}>{item.quantity} units</span>
                  </td>
                  <td className="total-value">${(item.price * item.quantity).toFixed(2)}</td>
                  <td>{item.supplier}</td>
                  <td>
                    <span className={`status-badge ${getStatusBadge(item.status)}`}>
                      {item.status.replace("_", " ")}
                    </span>
                  </td>
                  <td>{new Date(item.updatedAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default StockReport
