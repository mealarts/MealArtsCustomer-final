package com.mealarts.Helpers.Utils;

import java.util.ArrayList;

public class OrderUtils {

    private String OrderId, VendorName, VendorLocation, VendorId, OrderTotal, OrderItems, Qty, OrderDate, OrderStatus;
    private ArrayList<MenuUtils> OrderItemList;
    private Boolean isRated;

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getVendorLocation() {
        return VendorLocation;
    }

    public void setVendorLocation(String vendorLocation) {
        VendorLocation = vendorLocation;
    }

    public String getOrderTotal() {
        return OrderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        OrderTotal = orderTotal;
    }

    public String getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(String orderItems) {
        OrderItems = orderItems;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public ArrayList<MenuUtils> getOrderItemList() {
        return OrderItemList;
    }

    public void setOrderItemList(ArrayList<MenuUtils> orderItemList) {
        OrderItemList = orderItemList;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public Boolean getRated() {
        return isRated;
    }

    public void setRated(Boolean rated) {
        isRated = rated;
    }

    public String getVendorId() {
        return VendorId;
    }

    public void setVendorId(String vendorId) {
        VendorId = vendorId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }
}
