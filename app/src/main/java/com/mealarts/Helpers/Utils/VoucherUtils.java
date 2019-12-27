package com.mealarts.Helpers.Utils;

public class VoucherUtils {

    private String VoucherCode, VoucherDesc, VoucherFromDate, VoucherToDate, DiscountType, VoucherImg;
    private Integer VoucherId, VoucherMinValue, DiscountValue, DiscountUpTo;

    public String getVoucherCode() {
        return VoucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        VoucherCode = voucherCode;
    }

    public String getVoucherDesc() {
        return VoucherDesc;
    }

    public void setVoucherDesc(String voucherDesc) {
        VoucherDesc = voucherDesc;
    }

    public String getVoucherFromDate() {
        return VoucherFromDate;
    }

    public void setVoucherFromDate(String voucherFromDate) {
        VoucherFromDate = voucherFromDate;
    }

    public String getVoucherToDate() {
        return VoucherToDate;
    }

    public void setVoucherToDate(String voucherToDate) {
        VoucherToDate = voucherToDate;
    }

    public Integer getVoucherMinValue() {
        return VoucherMinValue;
    }

    public void setVoucherMinValue(Integer voucherMinValue) {
        VoucherMinValue = voucherMinValue;
    }

    public String getDiscountType() {
        return DiscountType;
    }

    public void setDiscountType(String discountType) {
        DiscountType = discountType;
    }

    public Integer getDiscountValue() {
        return DiscountValue;
    }

    public void setDiscountValue(Integer discountValue) {
        DiscountValue = discountValue;
    }

    public Integer getVoucherId() {
        return VoucherId;
    }

    public void setVoucherId(Integer voucherId) {
        VoucherId = voucherId;
    }

    public Integer getDiscountUpTo() {
        return DiscountUpTo;
    }

    public void setDiscountUpTo(Integer discountUpTo) {
        DiscountUpTo = discountUpTo;
    }

    public String getVoucherImg() {
        return VoucherImg;
    }

    public void setVoucherImg(String voucherImg) {
        VoucherImg = voucherImg;
    }
}
