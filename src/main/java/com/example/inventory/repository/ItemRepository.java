package com.example.inventory.repository;

import com.example.inventory.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Sort items by quantity using SQL
    List<Item> findAllByOrderByQuantityAsc();

    List<Item> findAllByOrderByQuantityDesc();

    // Native query: get all items
    @Query(value = "SELECT * FROM items", nativeQuery = true)
    List<Item> findAllNative();

    // Native query: filter items by category (text column on items)
    @Query(value = "SELECT * FROM items WHERE category = :category", nativeQuery = true)
    List<Item> findByCategoryNative(@Param("category") String category);

    // Native query: low-stock items with configurable threshold
    @Query(value = "SELECT * FROM items WHERE quantity < :threshold", nativeQuery = true)
    List<Item> findLowStockItemsNative(@Param("threshold") int threshold);

    // Native query: low-stock items strictly below 5
    @Query(value = "SELECT * FROM items WHERE quantity < 5", nativeQuery = true)
    List<Item> findLowStockItemsFixedNative();

    // Native query: total quantity per category
    @Query(value = "SELECT category, SUM(quantity) AS total_quantity FROM items GROUP BY category", nativeQuery = true)
    List<Object[]> findTotalQuantityPerCategoryNative();

    // Native query: top 5 categories by number of items
    @Query(value = "SELECT category, COUNT(*) AS item_count FROM items GROUP BY category ORDER BY item_count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopCategoriesByItemCountNative();
}

