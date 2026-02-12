package com.example.inventory.dto;

public class CategoryItemCountSummary {

    private String category;
    private Long itemCount;

    public CategoryItemCountSummary(String category, Long itemCount) {
        this.category = category;
        this.itemCount = itemCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }
}

