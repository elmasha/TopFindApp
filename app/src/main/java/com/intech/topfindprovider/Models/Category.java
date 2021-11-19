package com.intech.topfindprovider.Models;

public class Category {
private String category,category_ID;

    public Category() {
    }

    public Category(String category, String category_ID) {
        this.category = category;
        this.category_ID = category_ID;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }
}
