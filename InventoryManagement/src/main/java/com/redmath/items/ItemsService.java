package com.redmath.items;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemsService {
    private final ItemsRepository itemsRepository;

    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public List<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    public Items getItemById(Long id) {
        return itemsRepository.findById(id).orElseThrow();
    }

    public Items createItem(Items item) {
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return itemsRepository.save(item);
    }

    public Items updateItem(Long id, Items updatedItem) {
        return itemsRepository.findById(id).map(item -> {
            if (updatedItem.getName() != null) item.setName(updatedItem.getName());
            if (updatedItem.getPrice() != null) item.setPrice(updatedItem.getPrice());
            if (updatedItem.getSupplier() != null) item.setSupplier(updatedItem.getSupplier());
            if (updatedItem.getStatus() != null) item.setStatus(updatedItem.getStatus());
            item.setUpdatedAt(LocalDateTime.now());
            return itemsRepository.save(item);
        }).orElse(null);
    }

    public void deleteItem(Long id) {
        itemsRepository.deleteById(id);
    }

    public Items stockInward(Long id, int quantity) {
        Items item = itemsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + id));
        item.setQuantity(item.getQuantity() + quantity);
        item.setUpdatedAt(LocalDateTime.now());
        return itemsRepository.save(item);
    }

    public Items stockOutward(Long id, int quantity) {
        Items item = itemsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + id));

        if (item.getQuantity() < quantity) {
            throw new ExceptionUtility(
                    "Insufficient stock. Available: " + item.getQuantity() + ", Requested: " + quantity
            );
        }

        item.setQuantity(item.getQuantity() - quantity);
        item.setUpdatedAt(LocalDateTime.now());
        return itemsRepository.save(item);
    }

    public List<Items> getStockReport() {
        return itemsRepository.findAll();
    }
}
