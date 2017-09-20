package com.parasme.swopinfo.model;

/**
 * Created by SoNu on 7/14/2017.
 */

public class Category {
    int categoryId, categoryIcon;
    String categoryName;
    boolean isCategoryChecked;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isCategoryChecked() {
        return isCategoryChecked;
    }

    public void setCategoryChecked(boolean categoryChecked) {
        isCategoryChecked = categoryChecked;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
