package deafop.srhr.video.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.multidex.BuildConfig;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import deafop.srhr.video.Login.ItemUser;
import deafop.srhr.video.Login.LoadLogin;
import deafop.srhr.video.Login.LoginActivity;
import deafop.srhr.video.Login.LoginListener;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.Receiver.LoadNemosofts;
import deafop.srhr.video.Receiver.NemosoftsListener;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.SharedPref.SharedPre;
import deafop.srhr.video.asyncTask.LoadAbout;
import deafop.srhr.video.interfaces.AboutListener;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    SharedPre sharedPre;
    Methods methods;

    CardView Logo;
    Animation animation;
    TextView Logo_text;


    IInAppBillingService mService;
    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;
    private BillingProcessor bp;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPre = new SharedPre(this);
        if (!sharedPre.getIsFirstPurchaseCode()) {
            sharedPre.getPurchaseCode();
            sharedPre.getPurchase();
            initBuy();
        }
        if (sharedPre.getNightMode()) {
            setTheme(R.style.AppTheme2);
            Setting.Dark_Mode = true;
        } else {
            setTheme(R.style.AppTheme);
            Setting.Dark_Mode = false;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animation = AnimationUtils.loadAnimation(this, R.anim.smalltobig);

        Logo = findViewById(R.id.logo);
        Logo.startAnimation(animation);
        Logo_text = findViewById(R.id.logo_text);
        Logo_text.startAnimation(animation);

        methods = new Methods(this);

        if (methods.isNetworkAvailable()) {
            loadAboutData();
        } else {
            IntActivity();
        }
    }

    private void IntActivity() {
        Intent main = new Intent(SplashActivity.this, intActivity.class);
        startActivity(main);
        finish();
    }


    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    Toast.makeText(SplashActivity.this, "load About Data", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            loadSettings();
                           // Loadnemosofts();
                            sharedPre.setPurchase();
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.err_server));
                    }
                }
            });
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.error_connect_net_tryagain));
        }
    }

    public void Loadnemosofts() {
        if (sharedPre.getIsFirstPurchaseCode()) {
            LoadNemosofts loadAbout = new LoadNemosofts(SplashActivity.this, new NemosoftsListener() {
                @Override
                public void onStart() {
                    Toast.makeText(SplashActivity.this, "load Settings", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (!BuildConfig.APPLICATION_ID.equals(Setting.itemAbout.getPackage_name())) {
                                errorDialog(getString(R.string.error_maxmux_key), getString(R.string.create_maxmux_key));
                            } else {
                                sharedPre.setIsFirstPurchaseCode(false);
                                sharedPre.setPurchaseCode(Setting.itemAbout);
                                loadSettings();
                            }
                        } else {
                            errorDialog(getString(R.string.error_maxmux_key), message);
                        }
                    } else {
                        errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.error_connect_net_tryagain));
                    }
                }
            });
            loadAbout.execute();
        } else {
            sharedPre.getPurchaseCode();
            loadSettings();
        }
    }

    public void loadSettings() {
        if (sharedPre.getIsFirst()) {
            openLoginActivity();
        } else {
            if (!sharedPre.getIsAutoLogin()) {
                thiva();
            } else {
                if (methods.isNetworkAvailable()) {
                    loadLogin();
                } else {
                    thiva();
                }
            }
        }
    }

    private void openLoginActivity() {
        Intent intent;
        if (Setting.isLoginOn && sharedPre.getIsFirst()) {
            sharedPre.setIsFirst(false);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void thiva() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPre.getPurchase();
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder  alertDialog ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Setting.Dark_Mode){
                alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog2);
            }else {
                alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
            }
        } else {
            alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }


    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {

                }
                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Setting.itemUser = new ItemUser(user_id, user_name, sharedPre.getEmail(), "");
                            Setting.isLogged = true;
                            thiva();
                        } else {
                            thiva();
                        }
                    } else {
                        thiva();
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_LOGIN, 0, "", "", "", "", "", "", "", "", "", sharedPre.getEmail(), sharedPre.getPassword(), "", "", "", "", null));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void initBuy() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        if(!BillingProcessor.isIabServiceAvailable(this)) {
        }

        bp = new BillingProcessor(this, Setting.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
            }
            @Override
            public void onBillingInitialized() {
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mServiceConn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTextViews() {
        bp.loadOwnedPurchasesFromGoogle();
        if(isSubscribe(Setting.SUBSCRIPTION_ID)){
            Setting.getPurchases = true;
        } else{
            Setting.getPurchases = false;
        }
    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {
            return null;
        }
        try{
            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isSubscribe(String SUBSCRIPTION_ID_CHECK){

        if (!bp.isSubscribed(Setting.SUBSCRIPTION_ID)) {
            return false;
        }

        Bundle b =  getPurchases();
        if (b==null) {
            return  false;
        }
        if( b.getInt("RESPONSE_CODE") == 0){
            ArrayList<String>  purchaseDataList =
                    b.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            if(purchaseDataList == null){
                return  false;
            }
            if(purchaseDataList.size()==0){
                return  false;
            }
            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);

                try {
                    JSONObject rowOne = new JSONObject(purchaseData);
                    String  productId =  rowOne.getString("productId") ;

                    if (productId.equals(SUBSCRIPTION_ID_CHECK)){

                        Boolean  autoRenewing =  rowOne.getBoolean("autoRenewing");
                        if (autoRenewing){
                            return  true;
                        }else{
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            if (tsLong > (purchaseTime + (Setting.SUBSCRIPTION_DURATION*86400)) ){
                                Toast.makeText(SplashActivity.this, "", Toast.LENGTH_SHORT).show();
                                return  false;
                            }else{
                                return  true;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else{
            return false;
        }

        return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

}