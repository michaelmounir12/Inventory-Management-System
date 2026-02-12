package com.example.inventory.service;

import com.example.inventory.dto.CategoryItemCountSummary;
import com.example.inventory.dto.CategoryQuantitySummary;
import com.example.inventory.exception.ItemNotFoundException;
import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(Item item) {
        item.setId(null); // ensure a new entity
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item updated) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setQuantity(updated.getQuantity());
        existing.setLocation(updated.getLocation());

        return itemRepository.save(existing);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }

    public List<Item> listAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Flag items as low stock when quantity < threshold.
     * Returns the list of currently low-stock items.
     */
    public List<Item> flagLowStockItems(int threshold) {
        List<Item> all = itemRepository.findAll();
        for (Item item : all) {
            boolean isLow = item.getQuantity() != null && item.getQuantity() < threshold;
            item.setLowStock(isLow);
        }
        itemRepository.saveAll(all);
        return itemRepository.findLowStockItemsNative(threshold);
    }

    /**
     * Get low-stock items without re-flagging (uses quantity threshold).
     */
    public List<Item> getLowStockItems(int threshold) {
        return itemRepository.findLowStockItemsNative(threshold);
    }

    // ----- Optimized query helpers / algorithms -----

    /**
     * Get items sorted by quantity using SQL ordering.
     */
    public List<Item> getItemsSortedByQuantity(boolean ascending) {
        return ascending
                ? itemRepository.findAllByOrderByQuantityAsc()
                : itemRepository.findAllByOrderByQuantityDesc();
    }

    /**
     * Calculate total quantity per category using SQL GROUP BY
     * and map results into DTOs with a HashMap for efficiency.
     */
    public List<CategoryQuantitySummary> getTotalQuantityPerCategory() {
        List<Object[]> rows = itemRepository.findTotalQuantityPerCategoryNative();
        Map<String, Long> totals = new HashMap<>();

        for (Object[] row : rows) {
            String category = (String) row[0];
            Long totalQty = row[1] == null ? 0L : ((Number) row[1]).longValue();
            totals.put(category, totalQty);
        }

        List<CategoryQuantitySummary> result = new ArrayList<>(totals.size());
        for (Map.Entry<String, Long> entry : totals.entrySet()) {
            result.add(new CategoryQuantitySummary(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Return top 5 categories with the highest number of items.
     * Uses SQL GROUP BY / ORDER BY / LIMIT and DTO mapping.
     */
    public List<CategoryItemCountSummary> getTopCategoriesByItemCount() {
        List<Object[]> rows = itemRepository.findTopCategoriesByItemCountNative();
        List<CategoryItemCountSummary> result = new ArrayList<>(rows.size());

        for (Object[] row : rows) {
            String category = (String) row[0];
            Long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            result.add(new CategoryItemCountSummary(category, count));
        }

        return result;
    }
}

