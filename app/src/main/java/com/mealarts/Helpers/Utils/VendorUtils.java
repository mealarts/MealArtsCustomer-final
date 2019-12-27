package com.mealarts.Helpers.Utils;

import java.util.ArrayList;

public class VendorUtils {

    private String VendorId, VendorName, VendorImg, VendorCat, DistanceKm, PackingCharge,  DeliveryCharge, Speciality, Cuisines,
            Rating, RatingCount, City, Area, VendorLat, VendorLong,isGST,GST_Perc,Pack_charge;
    private ArrayList<MenuUtils> menuArray;
    private Boolean isSelected;

    public String getPack_charge() {
        return Pack_charge;
    }

    public void setPack_charge(String packCharge) {
        Pack_charge = packCharge;
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

    public String getVendorImg() {
        return VendorImg;
    }

    public void setVendorImg(String vendorImg) {
        VendorImg = vendorImg;
    }

    public ArrayList<MenuUtils> getMenuArray() {
        return menuArray;
    }

    public void setMenuArray(ArrayList<MenuUtils> menuArray) {
        this.menuArray = menuArray;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public String getCuisines() {
        return Cuisines;
    }

    public void setCuisines(String cuisines) {
        Cuisines = cuisines;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getVendorCat() {
        return VendorCat;
    }

    public void setVendorCat(String vendorCat) {
        VendorCat = vendorCat;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getDistanceKm() {
        return DistanceKm;
    }

    public void setDistanceKm(String distanceKm) {
        DistanceKm = distanceKm;
    }

    public String getDeliveryCharge() {
        return DeliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        DeliveryCharge = deliveryCharge;
    }

    public String getPackingCharge() {
        return PackingCharge;
    }

    public void setPackingCharge(String packingCharge) {
        PackingCharge = packingCharge;
    }

    public String getRatingCount() {
        return RatingCount;
    }

    public void setRatingCount(String ratingCount) {
        RatingCount = ratingCount;
    }

    public String getVendorLat() {
        return VendorLat;
    }

    public void setVendorLat(String vendorLat) {
        VendorLat = vendorLat;
    }

    public String getVendorLong() {
        return VendorLong;
    }

    public void setVendorLong(String vendorLong) {
        VendorLong = vendorLong;
    }
}
