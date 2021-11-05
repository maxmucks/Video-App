package deafop.srhr.video.Methods;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;
import deafop.srhr.video.Constant.Constant;
import deafop.srhr.video.item.itemVideo;
import okhttp3.MultipartBody;

import okhttp3.RequestBody;

import deafop.srhr.video.Login.ItemUser;
import deafop.srhr.video.Login.LoginActivity;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.MY_API;
import deafop.srhr.video.SharedPref.Setting;
import deafop.srhr.video.SharedPref.SharedPre;
import deafop.srhr.video.interfaces.InterAdListener;


public class Methods {

    private Context context;

    private InterAdListener interAdListener;

    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdFB;


    @SuppressLint("CommitPrefEdits")
    public Methods(Context context) {
        this.context = context;

    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
        if (Setting.getPurchases){
        }else {
            loadad();
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadad() {
        if (Setting.isAdmobInterAd) {
            interstitialAd = new InterstitialAd(context);
            AdRequest adRequest;
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                adRequest = new AdRequest.Builder()
                        .build();
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            }
            interstitialAd.setAdUnitId(Setting.ad_inter_id);
            interstitialAd.loadAd(adRequest);
        } else if (Setting.isFBInterAd) {
            interstitialAdFB = new com.facebook.ads.InterstitialAd(context, Setting.fb_ad_inter_id);
            interstitialAdFB.loadAd();
        }
    }


    public void showInter(final int pos, final String type) {
        if (Setting.getPurchases){
            interAdListener.onClick(pos, type);
        }else {
            if (Setting.isAdmobInterAd) {
                Setting.adCount = Setting.adCount + 1;
                if (Setting.adCount % Setting.adShow == 0) {
                    interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
                        @Override
                        public void onAdClosed() {
                            interAdListener.onClick(pos, type);
                            super.onAdClosed();
                        }
                    });
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    } else {
                        interAdListener.onClick(pos, type);
                    }
                    loadad();
                } else {
                    interAdListener.onClick(pos, type);
                }
            } else if (Setting.isFBInterAd) {
                Setting.adCount = Setting.adCount + 1;
                if (Setting.adCount % Setting.adShowFB == 0) {
                    interstitialAdFB.setAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                            interAdListener.onClick(pos, type);
                        }

                        @Override
                        public void onError(com.facebook.ads.Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onAdClicked(com.facebook.ads.Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {

                        }
                    });
                    if (interstitialAdFB.isAdLoaded()) {
                        interstitialAdFB.show();
                    } else {
                        interAdListener.onClick(pos, type);
                    }
                    loadad();
                } else {
                    interAdListener.onClick(pos, type);
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        }
    }

    private void showPersonalizedAds(LinearLayout linearLayout) {
        if (Setting.isAdmobBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0336997DCA346E1612B610471A578EEB").build();
            adView.setAdUnitId(Setting.ad_banner_id);
            adView.setAdSize(AdSize.BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        } else if (Setting.isFBBannerAd) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Setting.fb_ad_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adView.loadAd();
            linearLayout.addView(adView);
        }
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        if (Setting.isAdmobBannerAd) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            adView.setAdUnitId(Setting.ad_banner_id);
            adView.setAdSize(AdSize.BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        } else if (Setting.isFBBannerAd) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Setting.fb_ad_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adView.loadAd();
            linearLayout.addView(adView);
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (Setting.getPurchases){
        }else {
            if (isNetworkAvailable() && linearLayout != null) {
                if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                    showNonPersonalizedAds(linearLayout);
                } else {
                    showPersonalizedAds(linearLayout);
                }
            }
        }
    }

    public RequestBody getAPIRequest(String method, int page, String Nemosofts_key, String videoID, String searchText, String searchType, String catID, String mID, String CommentText, String istID, String rate, String email, String password, String name, String phone, String userID, String reportMessage, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new MY_API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {

            case Setting.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                break;

            case Setting.METHOD_REGISTER:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;

            case Setting.METHOD_COMMENT:
                jsObj.addProperty("news_id", videoID);
                jsObj.addProperty("user_name", name);
                jsObj.addProperty("comment_text", CommentText);
                break;

            case Setting.METHOD_COMMENT_ID:
                jsObj.addProperty("news_id", videoID);
                jsObj.addProperty("page", String.valueOf(page));
                break;


            case Setting.METHOD_LATEST:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_All_VIDEO:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_MOST_VIEWED:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_VIDEO_BY_CAT:
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_VIDEO_BY_CAT_NEW:
                jsObj.addProperty("cat_id", catID);
                break;


            case Setting.METHOD_VIDEO:
                jsObj.addProperty("video_id", videoID);
                break;

            case Setting.TAG_ROOT:
                jsObj.addProperty("key_id", Nemosofts_key);
                break;

        }

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", MY_API.toBase64(jsObj.toString()))
                .build();
    }

    public String format(Number number) {
        char[] arrc = new char[]{' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long l = number.longValue();
        double d = l;
        int n = (int)Math.floor((double)Math.log10((double)d));
        int n2 = n / 3;
        if (n >= 3 && n2 < arrc.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new DecimalFormat("#0.0").format(d / Math.pow((double)10.0, (double)(n2 * 3))));
            stringBuilder.append(arrc[n2]);
            return stringBuilder.toString();
        }
        return new DecimalFormat("#,##0").format(l);
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void showSnackBar(String message) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorAccent_Light));
        }
    }


    public void clickLogin() {
        if (Setting.isLogged) {
            logout((Activity) context);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
            logout_finish((Activity) context);
        }
    }
    public void logout_finish(Activity activity) {
        activity.finish();
    }

    public void changeAutoLogin(Boolean isAutoLogin) {
        SharedPre sharePref = new SharedPre(context);
        sharePref.setIsAutoLogin(isAutoLogin);
    }

    public void logout(Activity activity) {
        changeAutoLogin(false);
        Setting.isLogged = false;
        Setting.itemUser = new ItemUser("", "", "", "");
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }


    public GradientDrawable getGradientDrawable(int first, int second) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(15);
        gd.setColors(new int[]{first, second});
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gd.mutate();
        return gd;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            InputStream input;
            if(Setting.SERVER_URL.contains("https://")) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (Constant.isRTL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public void shareSong(itemVideo itemVideo) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_video));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.listening) + " - " + itemVideo.getVideo_title() + "\n\nvia " + context.getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_video)));
    }
}
