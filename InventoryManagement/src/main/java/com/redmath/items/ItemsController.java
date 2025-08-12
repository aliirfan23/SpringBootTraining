package com.redmath.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemsController {
    private final ItemsService itemsService;
    private final JwtDecoder jwtDecoder;

    public ItemsController(final ItemsService itemsService,final JwtDecoder jwtDecoder) {
        this.itemsService = java.util.Objects.requireNonNull(itemsService,"must not be null");
        this.jwtDecoder = java.util.Objects.requireNonNull(jwtDecoder,"must not be null");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getItemWithUserInfo(@RequestHeader("Authorization") String jwtHeader) {
        String token = jwtHeader.substring(jwtHeader.indexOf(" ") + 1);
        Jwt jwt = jwtDecoder.decode(token);

        String username = jwt.getSubject(); // comes from "sub"
        List<String> roles = jwt.getClaimAsStringList("roles");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "roles", roles
        ));
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

    // Stock inward endpoint
    @PostMapping("/{id}/inward")
    public ResponseEntity<?> stockInward(
            @PathVariable Long id,
            @RequestParam("quantity") int quantity) {
        try {
            Items updatedItem = itemsService.stockInward(id, quantity);
            return ResponseEntity.ok(updatedItem);
        } catch (NoSuchElementException e) {
            return handleItemNotFound(e);
        }
    }

    // Stock outward endpoint
    @PostMapping("/{id}/outward")
    public ResponseEntity<?> stockOutward(
            @PathVariable Long id,
            @RequestParam("quantity") int quantity) {
        try {
            Items updatedItem = itemsService.stockOutward(id, quantity);
            return ResponseEntity.ok(updatedItem);
        } catch (NoSuchElementException e) {
            return handleItemNotFound(e);
        } catch (ExceptionUtility e) {
            log.warn("Insufficient stock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Stock report endpoint
    @GetMapping("/stock-report")
    public ResponseEntity<List<Items>> getStockReport() {
        return ResponseEntity.ok(itemsService.getStockReport());
    }

    private ResponseEntity<Map<String, String>> handleItemNotFound(NoSuchElementException e) {
        log.warn("Item not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    // Add new exception handler
    @ExceptionHandler(ExceptionUtility.class)
    public ResponseEntity<?> handleInsufficientStock(ExceptionUtility e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}
