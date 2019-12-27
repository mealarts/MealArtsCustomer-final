package com.mealarts.Helpers.Utils;

public class CategoryUtils {

    private String categoryText, categoryId, startOrderSlot, endOrderSlot, deliverySlot;
    private Boolean Selected;

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public Boolean getSelected() {
        return Selected;
    }

    public void setSelected(Boolean selected) {
        Selected = selected;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getEndOrderSlot() {
        return endOrderSlot;
    }

    public void setEndOrderSlot(String endOrderSlot) {
        this.endOrderSlot = endOrderSlot;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public String getStartOrderSlot() {
        return startOrderSlot;
    }

    public void setStartOrderSlot(String startOrderSlot) {
        this.startOrderSlot = startOrderSlot;
    }
}
