import api from "./api"

export const inventoryApi = {
  // Get all items
  getAllItems: () => api.get("/items"),

  // Get item by ID
  getItemById: (id) => api.get(`/items/${id}`),

  // Create new item
  createItem: (itemData) => api.post("/items", itemData),

  // Update item
  updateItem: (id, itemData) => api.patch(`/items/${id}`, itemData),

  // Delete item
  deleteItem: (id) => api.delete(`/items/${id}`),

  // Stock movements
  stockInward: (id, quantity) => api.post(`/items/${id}/inward?quantity=${quantity}`),
  stockOutward: (id, quantity) => api.post(`/items/${id}/outward?quantity=${quantity}`),

  // Get stock report
  getStockReport: () => api.get("/items/stock-report"),

  // Get user info
  getUserInfo: () => api.get("/items/info"),
}
