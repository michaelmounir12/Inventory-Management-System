package com.example.inventory.controller;

import com.example.inventory.dto.CategoryItemCountSummary;
import com.example.inventory.dto.CategoryQuantitySummary;
import com.example.inventory.model.Item;
import com.example.inventory.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Create a new item.
     * POST /api/items
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item created = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing item.
     * PUT /api/items/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updated = itemService.updateItem(id, item);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete an item.
     * DELETE /api/items/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * List all items.
     * GET /api/items
     */
    @GetMapping
    public ResponseEntity<List<Item>> listItems() {
        return ResponseEntity.ok(itemService.listAllItems());
    }

    /**
     * Get single item by id (useful for clients).
     * GET /api/items/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    /**
     * Trigger low-stock flagging (on-demand automation).
     * POST /api/items/low-stock/refresh
     *
     * Flags items with quantity < 5 as low stock and returns them.
     */
    @PostMapping("/low-stock/refresh")
    public ResponseEntity<List<Item>> refreshLowStockItems() {
        List<Item> lowStock = itemService.flagLowStockItems(5);
        return ResponseEntity.ok(lowStock);
    }

    /**
     * Get current low-stock items (quantity < 5).
     * GET /api/items/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Item>> getLowStockItems() {
        List<Item> lowStock = itemService.getLowStockItems(5);
        return ResponseEntity.ok(lowStock);
    }

    /**
     * Get items sorted by quantity (asc/desc).
     * GET /api/items/sorted-by-quantity?order=asc|desc
     */
    @GetMapping("/sorted-by-quantity")
    public ResponseEntity<List<Item>> getItemsSortedByQuantity(
            @RequestParam(defaultValue = "asc") String order) {
        boolean ascending = !"desc".equalsIgnoreCase(order);
        return ResponseEntity.ok(itemService.getItemsSortedByQuantity(ascending));
    }

    /**
     * Get total quantity per category.
     * GET /api/items/summary/quantity-per-category
     */
    @GetMapping("/summary/quantity-per-category")
    public ResponseEntity<List<CategoryQuantitySummary>> getTotalQuantityPerCategory() {
        return ResponseEntity.ok(itemService.getTotalQuantityPerCategory());
    }

    /**
     * Get top 5 categories with highest number of items.
     * GET /api/items/summary/top-categories
     */
    @GetMapping("/summary/top-categories")
    public ResponseEntity<List<CategoryItemCountSummary>> getTopCategoriesByItemCount() {
        return ResponseEntity.ok(itemService.getTopCategoriesByItemCount());
    }
}

