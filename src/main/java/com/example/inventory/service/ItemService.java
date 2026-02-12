package com.example.inventory.service;

import com.example.inventory.exception.ItemNotFoundException;
import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

