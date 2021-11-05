package deafop.srhr.video.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;

import deafop.srhr.video.Login.ItemUser;
import deafop.srhr.video.Receiver.ItemNemosofts;


public class SharedPre {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String TAG_UID = "uid" ,TAG_USERNAME = "name", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_REMEMBER = "rem",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin";

    public SharedPre(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting_allinone", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getNightMode() {
        return sharedPreferences.getBoolean("night_mode", false);
    }

    public void setNightMode(Boolean state) {
        editor.putBoolean("night_mode", state);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }


    public void setIsFirstPurchaseCode(Boolean flag) {
        editor.putBoolean("firstopenpurchasecode", flag);
        editor.apply();
    }

    public Boolean getIsFirstPurchaseCode() {
        return sharedPreferences.getBoolean("firstopenpurchasecode", true);
    }

    public void setPurchaseCode(ItemNemosofts itemAbout) {
        editor.putString("purchase_code", itemAbout.getPurchase_code());
        editor.putString("product_name", itemAbout.getProduct_name());
        editor.putString("purchase_date", itemAbout.getPurchase_date());
        editor.putString("buyer_name", itemAbout.getBuyer_name());
        editor.putString("license_type", itemAbout.getLicense_type());
        editor.putString("nemosofts_key", itemAbout.getNemosofts_key());
        editor.putString("package_name", itemAbout.getPackage_name());
        editor.apply();
    }

    public void getPurchaseCode() {
        Setting.itemAbout = new ItemNemosofts(
                sharedPreferences.getString("purchase_code",""),
                sharedPreferences.getString("product_name",""),
                sharedPreferences.getString("purchase_date",""),
                sharedPreferences.getString("buyer_name",""),
                sharedPreferences.getString("license_type",""),
                sharedPreferences.getString("nemosofts_key",""),
                sharedPreferences.getString("package_name","")
        );
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, itemUser.getId());
        editor.putString(TAG_USERNAME, itemUser.getName());
        editor.putString(TAG_MOBILE, itemUser.getMobile());
        editor.putString(TAG_EMAIL, itemUser.getEmail());
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, password);
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Setting.itemUser = new ItemUser(sharedPreferences.getString(TAG_UID,""), sharedPreferences.getString(TAG_USERNAME,""), sharedPreferences.getString(TAG_EMAIL,""), sharedPreferences.getString(TAG_MOBILE,""));
    }

    public String getEmail() {
        return sharedPreferences.getString(TAG_EMAIL,"");
    }

    public String getPassword() {
        return sharedPreferences.getString(TAG_PASSWORD,"");
    }

    public Boolean isRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public void setPurchase() {
        editor.putBoolean("in_app", Setting.in_app);
        editor.putString("subscription_id", Setting.SUBSCRIPTION_ID);
        editor.putString("merchant_key", Setting.MERCHANT_KEY);
        editor.putInt("sub_dur", Setting.SUBSCRIPTION_DURATION);
        editor.apply();
    }

    public void getPurchase() {
        Setting.in_app = sharedPreferences.getBoolean("in_app", true);
        Setting.SUBSCRIPTION_ID = sharedPreferences.getString("subscription_id","SUBSCRIPTION_ID");
        Setting.MERCHANT_KEY = sharedPreferences.getString("merchant_key","MERCHANT_KEY");
        Setting.SUBSCRIPTION_DURATION = sharedPreferences.getInt("sub_dur",30);
    }

}
