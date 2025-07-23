package com.redmath.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemsController {
    private final ItemsService itemsService;

    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @GetMapping
    public List<Items> getAllItems() {
        return itemsService.getAllItems();
    }
    @PostMapping
    public Items createItem(@RequestBody Items item) {
        return itemsService.createItem(item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Items> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemsService.getItemById(id));
    }
    @Transactional
    @PatchMapping("/{id}")
    public Items updateItem(@PathVariable Long id, @RequestBody Items updatedItem) {
        return itemsService.updateItem(id, updatedItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemsService.deleteItem(id);
    }
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(NoSuchElementException e) {
        log.warn("Item not found: {}", e.getMessage(), e);
        return Map.of("issue", e.getMessage());
    }
}
