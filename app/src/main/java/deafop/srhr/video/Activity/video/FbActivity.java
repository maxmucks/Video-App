package deafop.srhr.video.Activity.video;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import deafop.srhr.video.Activity.FacebookPlayer;
import deafop.srhr.video.Constant.Constant;
import deafop.srhr.video.JSONParser.JSONParser;
import deafop.srhr.video.Methods.Methods;
import deafop.srhr.video.R;
import deafop.srhr.video.SharedPref.Setting;


public class FbActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_fb);

        methods = new Methods(this);

        position = getIntent().getIntExtra("pos2", 0);

        FacebookPlayer fbPlayerView = (FacebookPlayer) findViewById(R.id.fbPlayerView);
        fbPlayerView.setAutoPlay(true);
        fbPlayerView.setShowCaptions(false);
        fbPlayerView.setShowText(false);
        fbPlayerView.initialize(Constant.fb_app_id, Setting.arrayList.get(position).getVideo_url());
        fbPlayerView.setAutoPlayerHeight(this);

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
}
