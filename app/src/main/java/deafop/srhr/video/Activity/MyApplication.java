package deafop.srhr.video.Activity;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import deafop.srhr.video.DBHelper.DBHelper;
import deafop.srhr.video.R;


public class MyApplication extends Application {

    protected String mUserAgent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        try {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.onCreate(dbHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OneSignal.startInit(getApplicationContext())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        FirebaseAnalytics.getInstance(getApplicationContext());
        MobileAds.initialize(getApplicationContext(), getApplicationContext().getString(R.string.admob_app_id));
    }



}