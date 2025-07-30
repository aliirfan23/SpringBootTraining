"use client"

import { useState, useEffect } from "react"
import { inventoryApi } from "../../services/inventoryApi"
import Button from "../ui/Button"
import Input from "../ui/Input"
import "./AddEditItemModal.css"

const AddEditItemModal = ({ isOpen, onClose, item, onSuccess }) => {
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    quantity: "",
    supplier: "",
    status: "ACTIVE",
  })
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState({})

  useEffect(() => {
    if (item) {
      setFormData({
        name: item.name,
        price: item.price.toString(),
        quantity: item.quantity.toString(),
        supplier: item.supplier,
        status: item.status,
      })
    } else {
      setFormData({
        name: "",
        price: "",
        quantity: "",
        supplier: "",
        status: "ACTIVE",
      })
    }
    setErrors({})
  }, [item, isOpen])

  const validateForm = () => {
    const newErrors = {}

    if (!formData.name.trim()) {
      newErrors.name = "Name is required"
    }

    if (!formData.price || isNaN(Number(formData.price)) || Number(formData.price) < 0) {
      newErrors.price = "Valid price is required"
    }

    if (!formData.quantity || isNaN(Number(formData.quantity)) || Number(formData.quantity) < 0) {
      newErrors.quantity = "Valid quantity is required"
    }

    if (!formData.supplier.trim()) {
      newErrors.supplier = "Supplier is required"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!validateForm()) return

    setLoading(true)

    try {
      const payload = {
        name: formData.name.trim(),
        price: Number(formData.price),
        quantity: Number(formData.quantity),
        supplier: formData.supplier.trim(),
        status: formData.status,
      }

      if (item) {
        await inventoryApi.updateItem(item.itemId, payload)
      } else {
        await inventoryApi.createItem(payload)
      }

      onSuccess()
    } catch (error) {
      console.error("Failed to save item:", error)
      alert("Failed to save item. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: "" }))
    }
  }

  if (!isOpen) return null

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{item ? "Edit Item" : "Add New Item"}</h2>
          <button className="modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label htmlFor="name">Item Name</label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => handleChange("name", e.target.value)}
              placeholder="Enter item name"
              className={errors.name ? "error" : ""}
            />
            {errors.name && <span className="error-message">{errors.name}</span>}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="price">Price ($)</label>
              <Input
                id="price"
                type="number"
                step="0.01"
                min="0"
                value={formData.price}
                onChange={(e) => handleChange("price", e.target.value)}
                placeholder="0.00"
                className={errors.price ? "error" : ""}
              />
              {errors.price && <span className="error-message">{errors.price}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="quantity">Quantity</label>
              <Input
                id="quantity"
                type="number"
                min="0"
                value={formData.quantity}
                onChange={(e) => handleChange("quantity", e.target.value)}
                placeholder="0"
                className={errors.quantity ? "error" : ""}
              />
              {errors.quantity && <span className="error-message">{errors.quantity}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="supplier">Supplier</label>
            <Input
              id="supplier"
              value={formData.supplier}
              onChange={(e) => handleChange("supplier", e.target.value)}
              placeholder="Enter supplier name"
              className={errors.supplier ? "error" : ""}
            />
            {errors.supplier && <span className="error-message">{errors.supplier}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="status">Status</label>
            <select
              id="status"
              value={formData.status}
              onChange={(e) => handleChange("status", e.target.value)}
              className="form-select"
            >
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="OUT_OF_STOCK">Out of Stock</option>
              <option value="DISCONTINUED">Discontinued</option>
            </select>
          </div>

          <div className="modal-footer">
            <Button type="primary" onClick={onClose} className="btn-secondary">
              Cancel
            </Button>
            <Button type="submit" disabled={loading} className="btn-primary">
              {loading ? "Saving..." : item ? "Update Item" : "Add Item"}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default AddEditItemModal
