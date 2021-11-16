package com.intech.topfindprovider.Models;

public class Category {
private String Category,Category_ID;

    public Category() {
    }

    public Category(String category, String category_ID) {
        Category = category;
        Category_ID = category_ID;
    }


    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCategory_ID() {
        return Category_ID;
    }

    public void setCategory_ID(String category_ID) {
        Category_ID = category_ID;
    }
}
