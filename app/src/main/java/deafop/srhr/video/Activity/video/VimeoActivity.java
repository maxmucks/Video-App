package deafop.srhr.video.Activity.video;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;

import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class VimeoActivity extends AppCompatActivity {

    private WebView mWebView;

    Methods methods;
    int position;

    ProgressDialog pd;
    public Boolean load = true;


    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setAppTheme(R.style.AppTheme4);
        } else {
            setAppTheme(R.style.AppTheme3);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vimeo);

        pd = new ProgressDialog(this);
        pd.setMessage("loading");


        methods = new Methods(this);
        position = getIntent().getIntExtra("pos2", 0);

        mWebView = (WebView) findViewById(R.id.vimeo_player);
        mWebView.setWebChromeClient(new MyChrome());
        mWebView.setWebViewClient(new Browser_home());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        loadWebsite();


        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String json = JSONParser.okhttpPost(Setting.SERVER_URL, methods.getAPIRequest(Setting.METHOD_VIDEO, 0, "", Setting.arrayList.get(position).getId(), "", "", "", "", "", "","","","","","","","", null));
                return null;
            }
        }.execute();
    }

    private void loadWebsite() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            mWebView.setBackgroundColor(Color.BLACK);
            mWebView.loadUrl("http://player.vimeo.com/video/"+ Setting.arrayList.get(position).getVideo_id()+"?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800");
        } else {
            mWebView.setVisibility(View.GONE);
        }
    }

    class Browser_home extends WebViewClient {

        Browser_home() {
        }



        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitle(view.getTitle());
            super.onPageFinished(view, url);
        }
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (load){
                if (progress == 100) {
                    load = false;
                    pd.hide();
                } else {
                    pd.show();
                }
            }else {
                load = false;
            }

        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }



    private void setAppTheme(@StyleRes int style) {
        setTheme(style);
    }


}
