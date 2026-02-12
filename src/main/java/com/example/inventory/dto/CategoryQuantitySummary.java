package com.example.inventory.dto;

public class CategoryQuantitySummary {

    private String category;
    private Long totalQuantity;

    public CategoryQuantitySummary(String category, Long totalQuantity) {
        this.category = category;
        this.totalQuantity = totalQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}

