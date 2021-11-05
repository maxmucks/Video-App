package deafop.srhr.video.Activity.video;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import deafop.srhr.video.Activity.DMWebVideoView;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class DailymotionActivity extends AppCompatActivity {

    private DMWebVideoView mVideoView;
    Methods methods;
    int position;

    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setAppTheme(R.style.AppTheme4);
        } else {
            setAppTheme(R.style.AppTheme3);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailymotion_player);

        methods = new Methods(this);

        position = getIntent().getIntExtra("pos2", 0);

        mVideoView = ((DMWebVideoView) findViewById(R.id.dailymotion_player));
        mVideoView.setVideoId(Setting.arrayList.get(position).getVideo_id());
        mVideoView.setAutoPlay(true);
        mVideoView.load();

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String json = JSONParser.okhttpPost(Setting.SERVER_URL, methods.getAPIRequest(Setting.METHOD_VIDEO, 0, "", Setting.arrayList.get(position).getId(), "", "", "", "", "", "","","","","","","","", null));
                return null;
            }
        }.execute();

    }

    private void setAppTheme(@StyleRes int style) {
        setTheme(style);
    }

    @Override
    public void onBackPressed() {
        mVideoView.handleBackPress(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mVideoView.onResume();
        }
    }
}
