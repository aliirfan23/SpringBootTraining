"use client"

import { useState } from "react"
import { inventoryApi } from "../../services/inventoryApi"
import Button from "../ui/Button"
import Input from "../ui/Input"
import "./StockMovementModal.css"

const StockMovementModal = ({ isOpen, onClose, item, type, onSuccess }) => {
  const [quantity, setQuantity] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!item || !quantity) return

    const quantityNum = Number(quantity)
    if (isNaN(quantityNum) || quantityNum <= 0) {
      setError("Please enter a valid quantity")
      return
    }

    if (type === "outward" && quantityNum > item.quantity) {
      setError("Cannot remove more items than available in stock")
      return
    }

    setLoading(true)
    setError("")

    try {
      if (type === "inward") {
        await inventoryApi.stockInward(item.itemId, quantityNum)
      } else {
        await inventoryApi.stockOutward(item.itemId, quantityNum)
      }

      setQuantity("")
      onSuccess()
    } catch (error) {
      console.error(`Failed to perform ${type} movement:`, error)
      setError(`Failed to perform ${type} movement. Please try again.`)
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    setQuantity("")
    setError("")
    onClose()
  }

  if (!isOpen || !item) return null

  const newStockLevel =
    type === "inward" ? item.quantity + Number(quantity || 0) : item.quantity - Number(quantity || 0)

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{type === "inward" ? "➕ Stock Inward" : "➖ Stock Outward"}</h2>
          <button className="modal-close" onClick={handleClose}>
            ✕
          </button>
        </div>

        <div className="stock-item-info">
          <h3>{item.name}</h3>
          <p>
            Current Stock: <strong>{item.quantity} units</strong>
          </p>
          <p>Supplier: {item.supplier}</p>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label htmlFor="quantity">Quantity to {type === "inward" ? "Add" : "Remove"}</label>
            <Input
              id="quantity"
              type="number"
              min="1"
              max={type === "outward" ? item.quantity : undefined}
              value={quantity}
              onChange={(e) => {
                setQuantity(e.target.value)
                setError("")
              }}
              placeholder="Enter quantity"
              className={error ? "error" : ""}
            />
            {error && <span className="error-message">{error}</span>}
          </div>

          {quantity && (
            <div className="stock-preview">
              <p>
                New stock level will be: <strong>{newStockLevel} units</strong>
              </p>
            </div>
          )}

          <div className="modal-footer">
            <Button type="button" onClick={handleClose} className="btn-secondary">
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={loading || !quantity}
              className={type === "inward" ? "btn-success" : "btn-warning"}
            >
              {loading ? "Processing..." : `Confirm ${type === "inward" ? "Inward" : "Outward"}`}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default StockMovementModal
