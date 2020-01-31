package com.mealarts.AsyncTask;

public class URLServices {

    //private static String basicURL = "https://www.mealarts.com/test/Cust_json/";
    private static String basicURL = "https://www.mealarts.com/Cust_json/";
    public static String VendorImg = "https://www.mealarts.com/admin/assets/uploads/profile/";
    public static String DBoyImg = "https://www.mealarts.com/admin/assets/uploads/delivery/";
    public static String MenuImg = "https://www.mealarts.com/admin/assets/uploads/Menu/";
    public static String AddOnImg = "https://www.mealarts.com/admin/assets/uploads/addon/";
    public static String MenuDefaultImg = "https://www.mealarts.com/admin/assets/uploads/product/";
    public static String PromoOfferImg = "https://www.mealarts.com/admin/assets/uploads/offer/";

    public static String VisitorReg = basicURL + "visitor_reg.php";
    //mobile, password -> check SpreadSheet for Response

    public static String AddNewAddress = basicURL + "multiple_add.php";
    //mobile, password -> check SpreadSheet for Response

    public static String AddressList = basicURL + "list_address.php";
    //mobile, password -> check SpreadSheet for Response

    public static String Login = basicURL + "cust_login.php";
    //mobile, password -> check SpreadSheet for Response

    public static String Register = basicURL + "cust_register.php";
    //fname, lname, email, mobile, password -> check SpreadSheet for Response

    public static String UserProfile = basicURL + "user_profile.php";
    //customer_id -> check SpreadSheet for Response

    public static String UpdateProfile = basicURL + "update_profile.php";
    //fname, lname, email, mobile, password -> check SpreadSheet for Response

     public static String GetCategory = basicURL + "category.php";
    //-> check SpreadSheet for Response

     public static String GetCityArea = basicURL + "city_area.php";
    //-> check SpreadSheet for Response

    public static String VendorList = basicURL + "vendor_list.php";
    //latitude, longitude -> check SpreadSheet for Response

     public static String GetMenu = basicURL + "menu.php";
    //category_id, city_id, area_id, latitude, longitude -> check SpreadSheet for Response

    public static String GetCart = basicURL + "cart_test.php";/*"cart.php";*/
    //category_id, vendor_id, menu_id  -> check SpreadSheet for Response

    public static String ForgetPassword = basicURL + "forget_pass.php";
    //mobile -> check SpreadSheet for response

    public static String ChangePassword = basicURL + "change_pass.php";
    //mobile, new_pass -> check SpreadSheet for response

    public static String DeliverySlot = basicURL + "delivery_slot_time.php";
    //category_id -> check SpreadSheet for response

    public static String CheckoutOrder = basicURL + "add_cart_checkout_test.php";
    //cust_name, mobile, email, detail_address, address, msg_for_chef, category_id,
    //latitude, longitude, pincode, time_slot, customer_id, guest_id, total_amount, order_delivery_date,
    //if multiple menu then separated by comma menu_id, price, prod_unit, quantity, prod_name, vendor_id
    //-> check SpreadSheet for response

    public static String OrderPayment = basicURL + "add_cart_payment.php";
    //checkout_id, grand_total, del_charge, transaction_id, paytm_order_id, vendor_id-> check SpreadSheet for response

    public static String AllChef = basicURL + "show_review.php";
    //-> check SpreadSheet for response

    public static String SingleOrderDetails = basicURL + "single_order_details.php";
    //checkout_id-> check SpreadSheet for response

    public static String GiveReview = basicURL + "rating.php";
    //rating, review, customer_id, vendor_id, checkout_id-> check SpreadSheet for response

    public static String CancelOrder = basicURL + "cancel_order.php";
    //checkout_id-> check SpreadSheet for response

    public static String CustomerCancelOrder = basicURL + "cancel_customer_order.php";
    //checkout_id-> check SpreadSheet for response

    public static String ServerTime = basicURL + "server_time.php";
    //-> check SpreadSheet for response

    public static String SingleVendorDetail = basicURL + "single_vendor_details.php";
    //vendor_id-> check SpreadSheet for response

    public static String Promocode = basicURL + "promo.php";
    //vendor_id-> check SpreadSheet for response

    public static String OfferList = basicURL + "offer_list.php";
    //vendor_id-> check SpreadSheet for response

    public static String Offline = basicURL + "offline_status.php";
    //-> check SpreadSheet for response

    public static final boolean IS_PAYTM_STAGING = false;
    public static final String PAYTM_CALLBACK_TEST = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static final String PAYTM_CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static String PAYTM_CHECKSUM = "https://www.mealarts.com/PAYTM_Kit/generateChecksum.php";
    public static String PAYTM_CHECKSUM_TEST = "https://www.mealarts.com/test/PAYTM_Kit/generateChecksum.php";
}
