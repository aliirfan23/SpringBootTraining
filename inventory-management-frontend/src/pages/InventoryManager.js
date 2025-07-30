import React, { useState, useEffect } from 'react';
import api from '../../services/api';

const InventoryManager = () => {
  const [items, setItems] = useState([]);
  
  useEffect(() => {
    const fetchItems = async () => {
      const response = await api.get('/items');
      setItems(response.data);
    };
    fetchItems();
  }, []);

  // Add item form and table implementation here
  return (
    <div className="container mx-auto p-4">
      <h2 className="text-2xl font-bold mb-4">Inventory Management</h2>
      {/* Add your inventory management UI here */}
    </div>
  );
};

export default InventoryManager;