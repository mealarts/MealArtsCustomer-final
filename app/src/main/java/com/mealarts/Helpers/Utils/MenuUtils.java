package com.mealarts.Helpers.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuUtils {

    private String VendorId, VendorName, MenuId, ProductId, ProductName, OriginalPrice,
            SellingPrice, ProductImgDefault, ProductImg, Unit, MenuDesc, VegType, ProdIngredients,
            Qty, isGST, GST_Perc, gst_price, Pack_charge, MenuPrepTime, MenuOnOff;
    private Integer OfferPer;
    private ArrayList<JSONObject> AddOnsList;
    private Boolean isAddOns;

    public String getPack_charge() {
        return Pack_charge;
    }

    public void setPack_charge(String packCharge) {
        Pack_charge = packCharge;
    }

    public String getGst_price() {
        return gst_price;
    }

    public void setGst_price(String gstPrice) {
        gst_price = gstPrice;
    }

    public String getIsGST() {
        return isGST;
    }

    public void setIsGST(String gst) {
        isGST = gst;
    }

    public String getGST_Perc() {
        return GST_Perc;
    }

    public void setGST_Perc(String gstp) {
        GST_Perc = gstp;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getProductImg() {
        return ProductImg;
    }

    public void setProductImg(String productImg) {
        ProductImg = productImg;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getMenuDesc() {
        return MenuDesc;
    }

    public void setMenuDesc(String menuDesc) {
        MenuDesc = menuDesc;
    }

    public String getVegType() {
        return VegType;
    }

    public void setVegType(String vegType) {
        VegType = vegType;
    }

    public String getProdIngredients() {
        return ProdIngredients;
    }

    public void setProdIngredients(String prodIngredients) {
        ProdIngredients = prodIngredients;
    }

    public String getVendorId() {
        return VendorId;
    }

    public void setVendorId(String vendorId) {
        VendorId = vendorId;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getOriginalPrice() {
        return OriginalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        OriginalPrice = originalPrice;
    }

    public Integer getOfferPer() {
        return OfferPer;
    }

    public void setOfferPer(Integer offerPer) {
        OfferPer = offerPer;
    }

    public String getProductImgDefault() {
        return ProductImgDefault;
    }

    public void setProductImgDefault(String productImgDefault) {
        ProductImgDefault = productImgDefault;
    }

    public String getMenuPrepTime() {
        return MenuPrepTime;
    }

    public void setMenuPrepTime(String menuPrepTime) {
        MenuPrepTime = menuPrepTime;
    }

    public String getMenuOnOff() {
        return MenuOnOff;
    }

    public void setMenuOnOff(String menuOnOff) {
        MenuOnOff = menuOnOff;
    }

    public Boolean getAddOns() {
        return isAddOns;
    }

    public void setAddOns(Boolean addOns) {
        isAddOns = addOns;
    }

    public ArrayList<JSONObject> getAddOnsList() {
        return AddOnsList;
    }

    public void setAddOnsList(ArrayList<JSONObject> addOnsList) {
        AddOnsList = addOnsList;
    }
}
