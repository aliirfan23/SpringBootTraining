package com.redmath.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
}
